package com.simonjamesrowe.component.test;

import com.simonjamesrowe.component.test.postgresql.JpaEntity;
import com.simonjamesrowe.component.test.postgresql.JpaEntityRepository;
import com.simonjamesrowe.component.test.postgresql.WithPostgresContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TestContainersExtension.class)
@WithPostgresContainer
@DataJpaTest(properties = "spring.autoconfigure.exclude=")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestContainersExtensionTest {

    @Autowired
    private JpaEntityRepository jpaEntityRepository;

    private final static String ID = UUID.randomUUID().toString();

    @BeforeEach
    public void setup() {
        jpaEntityRepository.deleteAll();
        jpaEntityRepository.saveAll(Arrays.asList(new JpaEntity(ID, "val1", "val2")));
    }

    @Test
    public void firstTest() {
       doTest();
    }

    @Test
    public void secondTest() {
        doTest();
    }

    private void doTest() {
        Optional<JpaEntity> jpaEntity = jpaEntityRepository.findById(ID);
        assertThat(jpaEntity).isPresent();
        assertThat(jpaEntity.get()).hasFieldOrPropertyWithValue("id", ID);
        assertThat(jpaEntity.get()).hasFieldOrPropertyWithValue("fieldOne", "val1");
        assertThat(jpaEntity.get()).hasFieldOrPropertyWithValue("fieldTwo", "val2");
    }
}
