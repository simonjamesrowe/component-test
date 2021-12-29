package com.simonjamesrowe.component.test.vault;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Sets up a kafka container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith({VaultTestContainersExtension.class, SpringExtension.class})
public @interface WithVaultContainer {

}
