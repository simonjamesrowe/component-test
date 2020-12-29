package com.simonjamesrowe.component.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WiremockBootstrapContextTest extends BaseComponentTest {

    @Value("${test.uri}")
    private String testEndpoint;

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    @Test
    public void testCanAccessWiremockServer() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String,String> helloWorld = restTemplate.getForObject(testEndpoint, Map.class);
        assertThat(helloWorld).containsEntry("hello", "world");
        assertThat(wiremockPort).isNotEqualTo(8080);
    }

}
