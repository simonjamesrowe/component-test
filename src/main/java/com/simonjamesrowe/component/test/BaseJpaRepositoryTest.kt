package com.simonjamesrowe.component.test

import com.simonjamesrowe.component.test.postgresql.WithPostgresContainer
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@ExtendWith(TestContainersExtension::class)
@WithPostgresContainer
@AutoConfigureTestEntityManager
@DataJpaTest
abstract class BaseJpaRepositoryTest {
  @Autowired protected lateinit var em: TestEntityManager
}
