package com.simonjamesrowe.component.test.redis;

import com.simonjamesrowe.component.test.TestContainer;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Sets up a redis container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestContainer(initializers = RedisTestContainersInitializer.class)
public @interface WithRedisContainer {

}
