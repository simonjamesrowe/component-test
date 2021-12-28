package com.simonjamesrowe.component.test.vault;

import com.simonjamesrowe.component.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.vault.core.VaultTemplate;

import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

@WithVaultContainer
@ComponentTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WithVaultContainerTest {

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
        vaultTemplate.write("secret/data/component-test", Map.of("data", Map.of(
                "val1", "Secret1",
                "val2", "Secret2",
                "val3", "Secret3",
                "nested.val", "NestedVal"
        )));
        var result = (Map<String, Object>) vaultTemplate.read("secret/data/component-test").getData().get("data");
        assertThat(result.get("val1")).isEqualTo("Secret1");
        assertThat(result.get("val2")).isEqualTo("Secret2");
        assertThat(result.get("val3")).isEqualTo("Secret3");
        assertThat(result.get("nested.val")).isEqualTo("NestedVal");
    }

}
