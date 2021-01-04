package com.simonjamesrowe.component.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

@AutoConfigureWireMock(stubs = "classpath:META-INF/**/*.json", port = 0)
@ExtendWith(TestContainersExtension.class)
@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"component-test", "wiremock"})
public abstract class BaseComponentTest {

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

    @BeforeEach
    public void waitForKafkaListeners() {
        optionalKafkaListenerEndpointRegistry.ifPresent(registry -> {
            for (MessageListenerContainer messageListenerContainer : registry.getListenerContainers()) {
                ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    2);
            }
        });
    }
}
