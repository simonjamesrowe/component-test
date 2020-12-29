package com.simonjamesrowe.component.test

import com.simonjamesrowe.component.test.mongo.WithMongoDBContainer
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate

@ExtendWith(TestContainersExtension::class)
@WithMongoDBContainer
@DataMongoTest
abstract class BaseMongoRepositoryTest {
  @Autowired protected lateinit var mongoTemplate: MongoTemplate
}
