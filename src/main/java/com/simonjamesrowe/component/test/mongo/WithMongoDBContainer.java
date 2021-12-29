package com.simonjamesrowe.component.test.mongo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Sets up a Mongo container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith({MongoDBTestContainersExtension.class, SpringExtension.class})
public @interface WithMongoDBContainer {
}
