package com.example.integration.school.bdd.steps;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {
	public static final String FIXTURE_1_NAME = "first student";
	public static final String FIXTURE_1_ID = "1";
	public static final String FIXTURE_2_NAME = "second student";
	public static final String FIXTURE_2_ID = "2";
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
	
	@Given("The database contains a few students")
	public void the_database_contains_a_few_students() {
		MongoCollection<Document> collection = mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);
		collection.insertOne(new Document().append("id",FIXTURE_1_ID).append("name",FIXTURE_1_NAME));
		collection.insertOne(new Document().append("id",FIXTURE_2_ID).append("name",FIXTURE_2_NAME));
	}
	
	@Given("The student is in the meantime removed from the database")
	public void the_student_is_in_the_meantime_removed_from_the_database() {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).deleteOne(Filters.eq("id",FIXTURE_1_ID));
	}

}
