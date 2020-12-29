package com.simonjamesrowe.component.test.elasticsearch;

import com.simonjamesrowe.component.test.BaseComponentTest;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@WithElasticsearchContainer
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WithElasticsearchContainerTest extends BaseComponentTest {

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    private JestClient jestClient;

    @BeforeEach
    void setUpJestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(
                new HttpClientConfig.Builder(elasticsearchUrl)
                        .multiThreaded(true)
                        .defaultMaxTotalConnectionPerRoute(1)
                        .maxTotalConnection(1)
                        .build());
        jestClient = factory.getObject();
    }

    @Test
    void contextLoadedAndPropertiesSet() {
        assertThat(elasticsearchUrl).isNotNull();
    }

    @Test
    void shouldIndexAndRetrieveDocument() throws IOException {
        String author = "{\"name\":\"Anton Chekhov\"}";
        String authorsIndex = "authors";
        String authorId = "1";

        JestResult createIndexResult = jestClient.execute(new CreateIndex.Builder(authorsIndex).build());
        JestResult indexAuthorResult = jestClient.execute(
                new Index.Builder(author)
                        .index(authorsIndex)
                        .type("author")
                        .id(authorId)
                        .build()
        );
        JestResult findAuthorByIdResult = jestClient.execute(new Get.Builder(authorsIndex, authorId).build());

        assertThat(createIndexResult.isSucceeded()).isTrue();
        assertThat(indexAuthorResult.isSucceeded()).isTrue();
        assertThat(findAuthorByIdResult.isSucceeded()).isTrue();
        assertThat(findAuthorByIdResult.getJsonObject().get("_source").toString()).isEqualTo(author);
    }
}
