package com.example.integration.school.bdd.steps;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {
	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";

	private MongoClient mongoClient;

	@Before
	public void setUp() {
		mongoClient = new MongoClient();
		mongoClient.getDatabase(DB_NAME).drop();
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}

	@Given("The database contains the students with the following values")
	public void the_database_contains_the_students_with_the_following_values(List<List<String>> dataTable) {
		MongoCollection<Document> collection = mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);
		dataTable.forEach(l -> collection.insertOne(new Document().append("id", l.get(0)).append("name", l.get(1))));
	}

}
