package com.simonjamesrowe.component.test.elasticsearch;

import com.simonjamesrowe.component.test.TestContainer;

import java.lang.annotation.*;

/**
 * Sets up an Elasticsearch container and sets environment variable elasticsearch.url
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestContainer(initializers = ElasticsearchTestContainerInitializer.class)
public @interface WithElasticsearchContainer {

}
