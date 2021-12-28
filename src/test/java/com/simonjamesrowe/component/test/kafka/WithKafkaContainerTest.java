package com.simonjamesrowe.component.test.kafka;

import com.simonjamesrowe.component.test.BaseComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@WithKafkaContainer
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Import({TestKafkaListener2.class, KafkaAutoConfiguration.class})
public class WithKafkaContainerTest extends BaseComponentTest {

    public WithKafkaContainerTest() {
        partitionCount = 1;
    }

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private TestKafkaListener2 testKafkaListener;

    @Test
    public void testKafkaContainer() {
        await().atMost(Duration.ofSeconds(60)).until(() -> {
                    kafkaTemplate.send("test", "key", "Hello World");
                    return testKafkaListener.getData() != null;
                }
        );
        assertThat(testKafkaListener.getData()).isEqualTo("Hello World");
    }
}

@Configuration
@Lazy
class TestKafkaListener2 {

    private String data;

    public String getData() {
        return data;
    }

    @KafkaListener(topics = "test", groupId = "test2")
    public void listen(@Payload String messagePayload) {
        data = messagePayload;
    }

}


