package com.simonjamesrowe.component.test.elasticsearch;

import com.simonjamesrowe.component.test.BaseComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WithElasticsearchContainer
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WithElasticsearchContainerTest extends BaseComponentTest {

    @Autowired
    private TestRepository testRepository;

    @Test
    void shouldIndexAndRetrieveDocument() {
        TestDocument doc1 = new TestDocument("1", "This is document 1");
        TestDocument doc2 = new TestDocument("2", "This is Document 2");
        testRepository.saveAll(List.of(doc1,doc2));

        var result = testRepository.findById("2");
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(doc2);
    }
}
