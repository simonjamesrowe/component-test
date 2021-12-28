package com.simonjamesrowe.component.test.postgresql;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "test_entity")
public class JpaEntity implements Serializable {

    JpaEntity() {
    }

    public JpaEntity(String id, String fieldOne, String fieldTwo) {
        this.id = id;
        this.fieldOne = fieldOne;
        this.fieldTwo = fieldTwo;
    }

    @Id
    private String id;
    private String fieldOne;
    private String fieldTwo;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldOne() {
        return fieldOne;
    }

    public void setFieldOne(String fieldOne) {
        this.fieldOne = fieldOne;
    }

    public String getFieldTwo() {
        return fieldTwo;
    }

    public void setFieldTwo(String fieldTwo) {
        this.fieldTwo = fieldTwo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaEntity jpaEntity = (JpaEntity) o;
        return Objects.equals(id, jpaEntity.id) &&
                Objects.equals(fieldOne, jpaEntity.fieldOne) &&
                Objects.equals(fieldTwo, jpaEntity.fieldTwo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fieldOne, fieldTwo);
    }
}
