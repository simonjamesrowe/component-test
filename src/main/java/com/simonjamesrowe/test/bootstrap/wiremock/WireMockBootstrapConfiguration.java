package com.simonjamesrowe.test.bootstrap.wiremock;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.cloud.contract.wiremock.WireMockStubMapping;
import org.springframework.cloud.contract.wiremock.file.ResourcesFileSource;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.SocketUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Profile("wiremock")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WireMockProperties.class)
@PropertySource("classpath:wiremock.properties")
public class WireMockBootstrapConfiguration implements SmartLifecycle {

    private static Boolean INITIALISED = false;

    static final String WIREMOCK_SERVER_BEAN_NAME = "wireMockServer";

    private static final Log log = LogFactory.getLog(org.springframework.cloud.contract.wiremock.WireMockConfiguration.class);

    @Autowired
    WireMockProperties wireMock;

    private static volatile boolean running;

    private static WireMockServer server;

    @Autowired(required = false)
    private Options options;

    @Autowired(required = false)
    private WireMockConfigurationCustomizer customizer;

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void init() throws IOException {
       /** if (INITIALISED) {
            return;
        }**/
        if (log.isDebugEnabled()) {
            log.debug("Running initialization of the WireMock configuration");
        }
        if (this.options == null) {
            com.github.tomakehurst.wiremock.core.WireMockConfiguration factory = WireMockSpring
                    .options();
            if (this.wireMock.getServer().getPort() != 8080) {
                factory.port(this.wireMock.getServer().getPort());
            }
            if (this.wireMock.getServer().getHttpsPort() != -1) {
                factory.httpsPort(this.wireMock.getServer().getHttpsPort());
            }
            registerFiles(factory);
            factory.notifier(new Slf4jNotifier(true));
            if (this.wireMock.getPlaceholders().isEnabled()) {
                factory.extensions(new ResponseTemplateTransformer(false));
            }
            this.options = factory;
            if (this.customizer != null) {
                this.customizer.customize(factory);
            }
        }
        reRegisterServerWithResetMappings();
        reRegisterBeans();
        updateCurrentServer();
        INITIALISED = true;
    }

    private void reRegisterBeans() {
        if (!this.beanFactory.containsBean(WIREMOCK_SERVER_BEAN_NAME)) {
            this.beanFactory.registerSingleton(WIREMOCK_SERVER_BEAN_NAME, this.server);
        } else {
            this.beanFactory.destroySingleton(WIREMOCK_SERVER_BEAN_NAME);
            this.beanFactory.registerSingleton(WIREMOCK_SERVER_BEAN_NAME, this.server);
        }
    }

    private void reRegisterServer() {
        if (log.isDebugEnabled()) {
            log.debug("Creating a new server at " + "http port ["
                    + this.wireMock.getServer().getPort() + "] and " + "https port ["
                    + this.wireMock.getServer().getHttpsPort() + "]");
        }
        if (this.isRunning()) {
            this.server.stop();
        }
        this.server = new WireMockServer(this.options);
        this.server.start();
        updateCurrentServer();
        logRegisteredMappings();
    }

    private void logRegisteredMappings() {
        if (log.isDebugEnabled()) {
            log.debug("WireMock server has [" + this.server.getStubMappings().size()
                    + "] stubs registered");
        }
    }

    void reRegisterServerWithResetMappings() {
        reRegisterServer();
        if (this.server.isRunning()) {
            resetMappings();
            updateCurrentServer();
        }
    }

    void resetMappings() {
        if (this.server.isRunning()) {
            this.server.resetAll();
            this.server.resetRequests();
            this.server.resetScenarios();
            WireMock.reset();
            WireMock.resetAllRequests();
            WireMock.resetAllScenarios();
            registerStubs();
            logRegisteredMappings();
        }
    }

    private void registerStubs() {
        if (log.isDebugEnabled()) {
            log.debug("Will register [" + this.wireMock.getServer().getStubs().length
                    + "] stubs");
        }
        for (String stubs : this.wireMock.getServer().getStubs()) {
            if (StringUtils.hasText(stubs)) {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                        this.resourceLoader);
                StringBuilder pattern = new StringBuilder(stubs);
                if (!stubs.contains("*")) {
                    if (!stubs.endsWith("/")) {
                        pattern.append("/");
                    }
                    pattern.append("**/*.json");
                }
                try {
                    for (Resource resource : resolver.getResources(pattern.toString())) {
                        try (InputStream inputStream = resource.getInputStream()) {
                            StubMapping stubMapping = WireMockStubMapping
                                    .buildFrom(StreamUtils.copyToString(inputStream,
                                            StandardCharsets.UTF_8));
                            this.server.addStubMapping(stubMapping);
                        }
                    }
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
    }

    private void registerFiles(
            com.github.tomakehurst.wiremock.core.WireMockConfiguration factory)
            throws IOException {
        List<Resource> resources = new ArrayList<>();
        for (String files : this.wireMock.getServer().getFiles()) {
            if (StringUtils.hasText(files)) {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                        this.resourceLoader);
                for (Resource resource : resolver.getResources(files)) {
                    if (resource.exists()) {
                        resources.add(resource);
                    }
                }
            }
        }
        if (!resources.isEmpty()) {
            ResourcesFileSource fileSource = new ResourcesFileSource(
                    resources.toArray(new Resource[0]));
            factory.fileSource(fileSource);
        }
    }

    @Override
    public void start() {
        if (isRunning()) {
            if (log.isDebugEnabled()) {
                log.debug("Server is already running");
            }
            return;
        }

            this.server.start();
            updateCurrentServer();

    }

    private void updateCurrentServer() {
        WireMock.configureFor(new WireMock(this.server));
        this.running = true;
        if (log.isDebugEnabled() && this.server.isRunning()) {
            log.debug("Started WireMock at port [" + this.server.port() + "]. It has ["
                    + this.server.getStubMappings().size() + "] mappings registered");
        }
    }

    @Override
    public void stop() {
        if (this.running) {
            this.server.stop();
            this.server = null;
            this.running = false;
            this.options = null;
            if (log.isDebugEnabled()) {
                log.debug("Stopped WireMock instance");
            }
            this.beanFactory.destroySingleton(WIREMOCK_SERVER_BEAN_NAME);
        } else if (log.isDebugEnabled()) {
            log.debug("Server already stopped");
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

}

@ConfigurationProperties("wiremock")
class WireMockProperties {

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void allocateRandomPortForWiremock() {
        if (getServer().port == 0) {
            registerPropertySourceForDynamicEntries(environment, "wiremock.server.port",
                    10000, 12500, "wiremock.server.port-dynamic");
        }
    }

    private void registerPropertySourceForDynamicEntries(
            ConfigurableEnvironment environment, String portProperty, int minPort,
            int maxPort, String dynamicPortProperty) {
        MutablePropertySources propertySources = environment.getPropertySources();
        addPropertySource(propertySources);
        Map<String, Object> source = ((MapPropertySource) propertySources.get("wiremock"))
                .getSource();
        int port = SocketUtils.findAvailableTcpPort(minPort, maxPort);
        source.put(portProperty,port);
        source.put(dynamicPortProperty, true);
        this.server.port = port;
    }

    private void addPropertySource(MutablePropertySources propertySources) {
        if (!propertySources.contains("wiremock")) {
            propertySources.addFirst(
                    new MapPropertySource("wiremock", new HashMap<String, Object>()));
        } else {
            // Move it up into first place
            org.springframework.core.env.PropertySource<?> wiremock = propertySources.remove("wiremock");
            propertySources.addFirst(wiremock);
        }
    }

    private Server server = new Server();

    private Placeholders placeholders = new Placeholders();

    private boolean restTemplateSslEnabled;

    private boolean resetMappingsAfterEachTest;

    public boolean isRestTemplateSslEnabled() {
        return this.restTemplateSslEnabled;
    }

    public void setRestTemplateSslEnabled(boolean restTemplateSslEnabled) {
        this.restTemplateSslEnabled = restTemplateSslEnabled;
    }

    public boolean isResetMappingsAfterEachTest() {
        return this.resetMappingsAfterEachTest;
    }

    public void setResetMappingsAfterEachTest(boolean resetMappingsAfterEachTest) {
        this.resetMappingsAfterEachTest = resetMappingsAfterEachTest;
    }

    public Server getServer() {
        return this.server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Placeholders getPlaceholders() {
        return this.placeholders;
    }

    public void setPlaceholders(Placeholders placeholders) {
        this.placeholders = placeholders;
    }

    public class Placeholders {

        /**
         * Flag to indicate that http URLs in generated wiremock stubs should be filtered
         * to add or resolve a placeholder for a dynamic port.
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }

    public static class Server {

        private int port = 0;

        private int httpsPort = -1;

        private String[] stubs = new String[0];

        private String[] files = new String[0];

        private boolean portDynamic = false;

        private boolean httpsPortDynamic = false;

        public int getPort() {
            return this.port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getHttpsPort() {
            return this.httpsPort;
        }

        public void setHttpsPort(int httpsPort) {
            this.httpsPort = httpsPort;
        }

        public String[] getStubs() {
            return this.stubs;
        }

        public void setStubs(String[] stubs) {
            this.stubs = stubs;
        }

        public String[] getFiles() {
            return this.files;
        }

        public void setFiles(String[] files) {
            this.files = files;
        }

        public boolean isPortDynamic() {
            return this.portDynamic;
        }

        public void setPortDynamic(boolean portDynamic) {
            this.portDynamic = portDynamic;
        }

        public boolean isHttpsPortDynamic() {
            return this.httpsPortDynamic;
        }

        public void setHttpsPortDynamic(boolean httpsPortDynamic) {
            this.httpsPortDynamic = httpsPortDynamic;
        }

    }


}

