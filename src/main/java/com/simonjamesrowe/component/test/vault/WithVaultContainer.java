package com.simonjamesrowe.component.test.vault;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * Sets up a kafka container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(VaultTestContainersExtension.class)
public @interface WithVaultContainer {

}
