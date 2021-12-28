package com.simonjamesrowe.component.test.mongo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;

public abstract class TestDataRunner implements CommandLineRunner {

    private final String mongoCollection;

    public TestDataRunner(String mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        mongoTemplate.remove(new Query(), mongoCollection);
        File testDataFile = new ClassPathResource(mongoCollection + ".json").getFile();
        ArrayNode jsonArray = (ArrayNode) new ObjectMapper().readTree(testDataFile);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonNode jsonObject = jsonArray.get(i);
            Document document = Document.parse(jsonObject.toString());
            mongoTemplate.insert(document, mongoCollection);
        }

    }


}
