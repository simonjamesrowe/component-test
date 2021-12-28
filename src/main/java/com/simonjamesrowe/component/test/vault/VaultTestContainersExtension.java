package com.simonjamesrowe.component.test.vault;


import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.vault.VaultContainer;

/**
 * Runs test containers for Vault
 * <p>
 * It will keep the containers running across multiple test classes unlike using the original implementation which uses @ClassRule
 */

public class VaultTestContainersExtension extends VaultContainer implements BeforeAllCallback {

    private static final String vaultToken = "00000000-0000-0000-0000-000000000000";
    private static VaultTestContainersExtension container;

    public VaultTestContainersExtension() {
        super("vault:1.4.1");
        withVaultToken(vaultToken);
    }

    public void start() {
        super.start();
        System.setProperty("spring.cloud.vault.host", DockerClientFactory.instance().dockerHostIpAddress());
        System.setProperty("spring.cloud.vault.port", String.valueOf(container.getMappedPort(8200)));
        System.setProperty("spring.cloud.vault.token", vaultToken);
        System.setProperty("spring.cloud.vault.authentication", "token");
        System.setProperty("spring.cloud.vault.scheme", "http");
        System.setProperty("spring.cloud.vault.uri", "http://" + DockerClientFactory.instance().dockerHostIpAddress()
                + ":" + container.getMappedPort(8200));
        System.setProperty("spring.cloud.vault.connection-timeout", "5000");
        System.setProperty("spring.cloud.vault.kv.backend-version", "2");
        System.setProperty("spring.cloud.vault.kv.enabled", "true");
        System.setProperty("spring.cloud.vault.kv.backend", "secret");
        System.setProperty("spring.cloud.vault.enabled", "true");
        System.setProperty("spring.cloud.vault.read-timeout", "15000");
    }

    private static VaultTestContainersExtension getInstance() {
        if (container == null) {
            container = new VaultTestContainersExtension();
        }
        return container;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getInstance().start();
    }
}
