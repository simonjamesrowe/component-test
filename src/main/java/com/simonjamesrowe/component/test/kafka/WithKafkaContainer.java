package com.simonjamesrowe.component.test.kafka;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Sets up an Kafka Container and sets spring property spring.kafka.bootstrap-servers
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith({KafkaTestContainerExtension.class, SpringExtension.class})
public @interface WithKafkaContainer {

}
