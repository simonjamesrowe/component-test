package com.simonjamesrowe.component.test.kafka;

import com.simonjamesrowe.component.test.TestContainer;

import java.lang.annotation.*;

/**
 * Sets up an Kafka Container and sets spring property spring.kafka.bootstrap-servers
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestContainer(initializers = KafkaTestContainerInitializer.class)
public @interface WithKafkaContainer {

}
