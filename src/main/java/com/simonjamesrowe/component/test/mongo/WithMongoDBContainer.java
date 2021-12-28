package com.simonjamesrowe.component.test.mongo;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * Sets up a Mongo container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(MongoDBTestContainersExtension.class)
public @interface WithMongoDBContainer {
}
