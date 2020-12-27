package com.simonjamesrowe.component.test.vault;

import static org.assertj.core.api.Java6Assertions.assertThat;

import com.simonjamesrowe.component.test.BaseComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@WithVaultContainer(secretPath = "secret/component-test", secretValues = {"val1=Secret1", "val2=Secret2", "val3=Secret3", "nested.val=NestedVal"})
public class WithVaultContainerTest extends BaseComponentTest {

    @Value("${spring.cloud.vault.host}")
    private String vaultHost;

    @Value("${spring.cloud.vault.port}")
    private String vaultPort;

    @Value("${val1}")
    private String val1;
    @Value("${val2}")
    private String val2;
    @Value("${val3}")
    private String val3;
    @Value("${nested.val}")
    private String nestedVal;


    @Test
    public void contextLoadedAndPropertiesSet() {
        assertThat(vaultHost).isNotNull();
        assertThat(vaultPort).isNotNull();
        assertThat(val1).isEqualTo("Secret1");
        assertThat(val2).isEqualTo("Secret2");
        assertThat(val3).isEqualTo("Secret3");
        assertThat(nestedVal).isEqualTo("NestedVal");
    }

}
