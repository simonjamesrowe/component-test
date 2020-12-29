package com.simonjamesrowe.component.test.postgresql;

import com.simonjamesrowe.component.test.TestContainer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.lang.annotation.*;

/**
 * Sets up a redis container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestContainer(initializers = PostgresqlTestContainersInitializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface WithPostgresContainer {

    int poolSize() default 2;

    String[] command() default {"postgres", "-c", "max_connections=500", "-c", "shared_buffers=256MB"};
}
