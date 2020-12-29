package com.simonjamesrowe.component.test.mongo;

import com.simonjamesrowe.component.test.TestContainer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets up a kafka container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestContainer(initializers = MongoDBTestContainersInitializer.class)
public @interface WithMongoDBContainer {}
