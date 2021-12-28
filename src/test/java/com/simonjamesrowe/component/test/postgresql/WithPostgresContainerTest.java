package com.simonjamesrowe.component.test.postgresql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@WithPostgresContainer
@DataJpaTest(properties = "spring.autoconfigure.exclude=")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

}
