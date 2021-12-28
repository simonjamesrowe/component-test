package com.simonjamesrowe.component.test.kafka;

import com.simonjamesrowe.component.test.BaseComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@WithEmbeddedKafka
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Import({TestKafkaListener.class, KafkaAutoConfiguration.class})
public class EmbeddedKafkaTest extends BaseComponentTest {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private TestKafkaListener testKafkaListener;

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

@Service
@Lazy
class TestKafkaListener {

    private String data;

    public String getData() {
        return data;
    }

    @KafkaListener(topics = "test", groupId = "test")
    public void listen(@Payload String messagePayload) {
        data = messagePayload;
    }

}
