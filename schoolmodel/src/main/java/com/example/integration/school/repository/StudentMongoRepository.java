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

	private MongoCollection<Document> collection;

	public StudentMongoRepository(MongoClient client,String databaseName, String collectionName) {
		collection = client.getDatabase(databaseName).getCollection(collectionName);
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
		collection.insertOne(new Document().append("id", student.getId()).append("name", student.getName()));
	}

	@Override
	public void delete(String id) {
		collection.deleteOne(Filters.eq("id", id));
	}

	private Student from_Document_to_Student(Document d) {
		return new Student(d.get("id").toString(), d.getString("name").toString());
	}

}
