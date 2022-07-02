package com.simonjamesrowe.component.test.kafka;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith({SingletonEmbeddedKafkaExtension.class})
@Repeatable(SingletonEmbeddedKafkas.class)
public @interface SingletonEmbeddedKafka {

    String name() default "default";

    boolean schemaRegistry() default false;

    String schemaRegistryProperty() default "schema.registry.url";

    String bootstrapServerProperty() default "spring.kafka.bootstrap-servers";

}
