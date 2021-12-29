package com.simonjamesrowe.component.test.postgresql;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Sets up a redis container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith({PostgresqlTestContainersExtension.class, SpringExtension.class})
public @interface WithPostgresContainer {


}
