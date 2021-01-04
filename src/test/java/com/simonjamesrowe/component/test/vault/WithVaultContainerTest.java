package com.simonjamesrowe.component.test.vault;

import static org.assertj.core.api.Java6Assertions.assertThat;

import com.simonjamesrowe.component.test.BaseComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.vault.config.VaultConfigTemplate;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultTemplate;

import java.util.Map;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@WithVaultContainer(secretPath = "secret/component-test", secretValues = {"val1=Secret1", "val2=Secret2", "val3=Secret3", "nested.val=NestedVal"})
public class WithVaultContainerTest extends BaseComponentTest {

    @Value("${spring.cloud.vault.host}")
    private String vaultHost;

    @Value("${spring.cloud.vault.port}")
    private String vaultPort;

    @Autowired
    private VaultTemplate vaultTemplate;

    @Test
    public void contextLoadedAndPropertiesSet() {
        assertThat(vaultHost).isNotNull();
        assertThat(vaultPort).isNotNull();
        var result = (Map<String, Object>) vaultTemplate.read("secret/data/component-test").getData().get("data");
        assertThat(result.get("val1")).isEqualTo("Secret1");
        assertThat(result.get("val2")).isEqualTo("Secret2");
        assertThat(result.get("val3")).isEqualTo("Secret3");
        assertThat(result.get("nested.val")).isEqualTo("NestedVal");
    }

}
