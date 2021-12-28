package com.simonjamesrowe.component.test.postgresql;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * Sets up a redis container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(PostgresqlTestContainersExtension.class)
public @interface WithPostgresContainer {


}
