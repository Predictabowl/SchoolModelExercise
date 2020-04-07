package com.example.integration.school.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import static com.example.integration.school.repository.StudentMongoRepository.*;

import com.example.integration.school.model.Student;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/*
 * Premise:
 * We assume that StudentMongoRepository, due to its very nature, will not be tested by an unit test but by an integration test.
 * Since it's an integration test we also assume that we don't make an exhaustive test case, and we only cover the interesting cases.
 * 
 * Questions:
 * - This class can be made with TDD? The test doesn't cover every option and don't give a full documentation of the class.
 * - How do we handle the coverage? We could not reach 100% coverage with this premise.
 * - Since tests are not exhaustive this Class should not be subject of mutation tests? Mutation tests are exclusive to unit tests?  
 */

public class StudentMongoRepositoryTestcontainersIT {

//	@SuppressWarnings("rawtypes")

//	@ClassRule
//	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3").withExposedPorts(27017);

	private MongoClient client;
	private StudentRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	@Before
	public void setup() {
//		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		// The following line is used only when a container with mongoDB is running in the background
		client = new MongoClient(new ServerAddress("localhost", 27017));
		studentRepository = new StudentMongoRepository(client);
		MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
		// we make sure the database is cleaned at the start of every test method
		database.drop();
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void test_findAll() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findAll()).containsExactly(new Student("1", "test1"), new Student("2", "test2"));
	}

	@Test
	public void test_findAll_when_database_is_empty() {
		assertThat(studentRepository.findAll()).isEmpty();
	}

	@Test
	public void test_findById_not_found() {
		assertThat(studentRepository.findById("1")).isNull();

	}

	@Test
	public void test_findById_found() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
	}

	@Test
	public void test_save() {
		Student student = new Student("1", "test1");
		studentRepository.save(student);
		assertThat(readAllStudentsFromDatabase()).containsExactly(student);
	}
	
	@Test
	public void test_delete() {
		addTestStudentToDatabase("1", "test1");
		studentRepository.delete("1");
		assertThat(readAllStudentsFromDatabase()).isEmpty();
	}

	private List<Student> readAllStudentsFromDatabase() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(d.get("id").toString(), d.get("name").toString())).collect(Collectors.toList());
	}

	private void addTestStudentToDatabase(String id, String name) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name));
	}

}
