package com.simonjamesrowe.component.test.postgresql;

import com.simonjamesrowe.component.test.TestContainersExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;


@ExtendWith(TestContainersExtension.class)
@WithPostgresContainer
@DataJpaTest(properties = "spring.autoconfigure.exclude=")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WithPostgresContainerTest {

    @Autowired
    private JpaEntityRepository jpaEntityRepository;

    @Autowired
    private DataSource dataSource;

    private final static String ID = UUID.randomUUID().toString();

    @BeforeEach
    public void setup() {
        jpaEntityRepository.saveAll(Arrays.asList(new JpaEntity(ID, "val1", "val2")));
    }

    @Test
    public void testRetrieveViaJpa() {
        Optional<JpaEntity> jpaEntity = jpaEntityRepository.findById(ID);
        assertThat(jpaEntity).isPresent();
        assertThat(jpaEntity.get()).hasFieldOrPropertyWithValue("id", ID);
        assertThat(jpaEntity.get()).hasFieldOrPropertyWithValue("fieldOne", "val1");
        assertThat(jpaEntity.get()).hasFieldOrPropertyWithValue("fieldTwo", "val2");
    }

    @Test
    public void test500ConnectionsByDefault() {
        IntStream.of(500).forEach(it -> {
            try {
                dataSource.getConnection();
            } catch (SQLException e) {
               fail("Should create new connection: " + it, e);
            }
        });
        assertThrows(Exception.class, () -> dataSource.getConnection());
    }
}
