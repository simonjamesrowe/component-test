package com.simonjamesrowe.test.bootstrap.wiremock;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;

/**
 * Disables https for spring cloud contract wiremock
 */
public class DisableWiremockHttps implements WireMockConfigurationCustomizer {

    @Override 
    public void customize(WireMockConfiguration config) {
        config.httpsPort(-1);
    }
}
