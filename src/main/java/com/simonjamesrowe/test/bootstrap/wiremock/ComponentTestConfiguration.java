package com.simonjamesrowe.test.bootstrap.wiremock;

import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("component-test")
public class ComponentTestConfiguration {

    @Bean
    public WireMockConfigurationCustomizer wiremockCustomizer() {
        return (config) -> {
            config.asynchronousResponseEnabled(false);
            config.httpsPort(-1);
        };
    }

}
