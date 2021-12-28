package com.simonjamesrowe.component.test.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document("mongoDocument")
public class MongoDocument {

    public MongoDocument() {
    }

    public MongoDocument(String fieldOne, Integer fieldTwo, Boolean fieldThree) {
        this.fieldOne = fieldOne;
        this.fieldTwo = fieldTwo;
        this.fieldThree = fieldThree;
    }

    @Id
    private String id;

    private String fieldOne;
    private Integer fieldTwo;
    private Boolean fieldThree;

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

    public Integer getFieldTwo() {
        return fieldTwo;
    }

    public void setFieldTwo(Integer fieldTwo) {
        this.fieldTwo = fieldTwo;
    }

    public Boolean getFieldThree() {
        return fieldThree;
    }

    public void setFieldThree(Boolean fieldThree) {
        this.fieldThree = fieldThree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoDocument that = (MongoDocument) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fieldOne, that.fieldOne) &&
                Objects.equals(fieldTwo, that.fieldTwo) &&
                Objects.equals(fieldThree, that.fieldThree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fieldOne, fieldTwo, fieldThree);
    }
}
