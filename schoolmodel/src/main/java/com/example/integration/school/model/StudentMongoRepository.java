package com.example.integration.school.model;

import java.util.List;
import org.bson.*;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;

public class StudentMongoRepository implements StudentRepository {

	private static final String STUDENT_COLLECTION_NAME = "student";
	private static final String SCHOOL_DB_NAME = "school";
	private MongoCollection<Document> collection;

	public StudentMongoRepository(MongoClient client) {
		collection = client.getDatabase(SCHOOL_DB_NAME).getCollection(STUDENT_COLLECTION_NAME);
	}
	
	@Override
	public List<Student> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Student findById(String Id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Student student) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub

	}

}
