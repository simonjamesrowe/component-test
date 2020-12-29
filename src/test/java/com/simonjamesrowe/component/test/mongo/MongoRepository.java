package com.simonjamesrowe.component.test.mongo;

import org.springframework.data.repository.CrudRepository;

public interface MongoRepository extends CrudRepository<MongoDocument, String> {
}
