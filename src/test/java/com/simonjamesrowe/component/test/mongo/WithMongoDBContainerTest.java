package com.simonjamesrowe.component.test.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@WithMongoDBContainer
@DataMongoTest(properties = "spring.autoconfigure.exclude=")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WithMongoDBContainerTest {

    @Autowired
    private MongoRepository mongoRepository;

    @BeforeEach
    public void beforeEach() {
        mongoRepository.saveAll(Arrays.asList(new MongoDocument("one", 2, false),
                new MongoDocument("two", 4, true)));
    }

    @Test
    public void mongoContainerIsInitialised() {
        assertThat(mongoRepository.count()).isEqualTo(2L);
    }
}
