package com.simonjamesrowe.component.test;

import com.simonjamesrowe.test.bootstrap.wiremock.WireMockBootstrapConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WiremockDisabledBootstrapContextTest {

    @Value("${wiremock.server.port:9999999}")
    private int wiremockPort;

    @Autowired(required = false)
    private WireMockBootstrapConfiguration wireMockBootstrapConfiguration;

    @Test
    public void testCanAccessWiremockServer() {
       assertThat(wireMockBootstrapConfiguration).isNull();
       assertThat(wiremockPort).isEqualTo(9999999);
    }

}
