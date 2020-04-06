package com.example.integration.school.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.*;

import com.example.integration.school.model.Student;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class StudentMongoRepository implements StudentRepository {

	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String SCHOOL_DB_NAME = "school";
	private MongoCollection<Document> collection;

	public StudentMongoRepository(MongoClient client) {
		collection = client.getDatabase(SCHOOL_DB_NAME).getCollection(STUDENT_COLLECTION_NAME);
	}

	@Override
	public List<Student> findAll() {
		return StreamSupport.stream(collection.find().spliterator(), false).map(this::from_Document_to_Student)
				.collect(Collectors.toList());
	}

	@Override
	public Student findById(String id) {
		Document d = collection.find(Filters.eq("id", id)).first();
		if (d == null)
			return null;
		return from_Document_to_Student(d);
	}

	@Override
	public void save(Student student) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub

	}

	private Student from_Document_to_Student(Document d) {
		return new Student((String) d.get("id"), (String) d.getString("name"));
	}

}
