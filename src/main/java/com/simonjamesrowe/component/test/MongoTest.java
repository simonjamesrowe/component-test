package com.simonjamesrowe.component.test;

import com.simonjamesrowe.component.test.mongo.WithMongoDBContainer;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.lang.annotation.*;

/**
 * Sets up a redis container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WithMongoDBContainer
@DataMongoTest
public @interface MongoTest {

}
