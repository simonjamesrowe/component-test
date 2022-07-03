package com.simonjamesrowe.component.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

@ComponentTest
public abstract class BaseComponentTest {

    protected Integer partitionCount = 2;

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WireMockServer wireMockServer;

    @Autowired
    protected Environment environment;

    @Autowired(required = false)
    protected Optional<KafkaListenerEndpointRegistry> optionalKafkaListenerEndpointRegistry;

    @BeforeEach
    public void setupRestAssured() {
        wireMockServer.resetRequests();
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.defaultParser = Parser.JSON;
    }
}
