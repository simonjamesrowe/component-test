package com.simonjamesrowe.component.test.postgresql;

import org.springframework.data.repository.CrudRepository;

public interface JpaEntityRepository extends CrudRepository<JpaEntity, String> {
}
