package com.simonjamesrowe.component.test.elasticsearch;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Sets up an Elasticsearch container and sets environment variable elasticsearch.url
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith({ElasticsearchTestContainerExtension.class, SpringExtension.class})
public @interface WithElasticsearchContainer {

}
