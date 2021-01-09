package com.simonjamesrowe.component.test.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TestRepository extends ElasticsearchRepository<TestDocument, String> {
}
