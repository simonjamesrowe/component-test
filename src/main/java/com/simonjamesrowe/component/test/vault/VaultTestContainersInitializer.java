package com.simonjamesrowe.component.test.vault;


import com.simonjamesrowe.component.test.TestContainersExtension;
import org.apache.commons.lang3.StringUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.vault.VaultContainer;

/**
 * Runs test containers for Vault
 * <p>
 * It will keep the containers running across multiple test classes unlike using the original implementation which uses @ClassRule
 */

public class VaultTestContainersInitializer  {

    private static int vaultPort;
    private static String vaultToken = "00000000-0000-0000-0000-000000000000";
    private static boolean initialized = false;
    private static VaultContainer vaultContainer;

    public VaultTestContainersInitializer(WithVaultContainer withVaultContainer) {
        if (!initialized) {
            vaultContainer = new VaultContainer("vault:1.4.1");
            vaultContainer.withVaultToken(vaultToken);
            if (StringUtils.isNotBlank(withVaultContainer.secretPath())) {
                vaultContainer = vaultContainer.withSecretInVault(withVaultContainer.secretPath(), "test=ing", withVaultContainer.secretValues());
            }
            initialize();
        }
    }

    public void initialize() {
        if (!initialized) {
            TestContainersExtension.startContainer(vaultContainer);
            vaultPort = vaultContainer.getMappedPort(8200);
        }
        initialized = true;
        System.setProperty("spring.cloud.vault.host", DockerClientFactory.instance().dockerHostIpAddress());
        System.setProperty("spring.cloud.vault.port", String.valueOf(vaultPort));
        System.setProperty("spring.cloud.vault.token", vaultToken);
        System.setProperty("spring.cloud.vault.authentication", "token");
        System.setProperty("spring.cloud.vault.scheme", "http");
        System.setProperty("spring.cloud.vault.uri", "http://" + DockerClientFactory.instance().dockerHostIpAddress() + ":" + vaultPort);
        System.setProperty("spring.cloud.vault.connection-timeout", "5000");
        System.setProperty("spring.cloud.vault.kv.backend-version", "2");
        System.setProperty("spring.cloud.vault.kv.enabled", "true");
        System.setProperty("spring.cloud.vault.kv.backend", "secret");
        System.setProperty("spring.cloud.vault.enabled", "true");
        System.setProperty("spring.cloud.vault.read-timeout", "15000");
    }
}
