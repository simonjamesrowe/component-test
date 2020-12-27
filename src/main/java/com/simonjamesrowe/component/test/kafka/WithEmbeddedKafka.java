package com.simonjamesrowe.component.test.kafka;

import org.springframework.kafka.test.context.EmbeddedKafka;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers", brokerProperties = "auto.create.topics.enable=true")
public @interface WithEmbeddedKafka {

}
