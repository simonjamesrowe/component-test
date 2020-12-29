package com.simonjamesrowe.component.test.vault;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.simonjamesrowe.component.test.TestContainer;
import org.springframework.test.context.ActiveProfiles;

/**
 * Sets up a kafka container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestContainer(initializers = VaultTestContainersInitializer.class)
@ActiveProfiles("vault-component-test")
public @interface WithVaultContainer {

    String secretPath() default "{}";

    String[] secretValues() default {};
}
