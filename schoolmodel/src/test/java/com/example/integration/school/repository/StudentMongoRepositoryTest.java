package com.example.integration.school.repository;

import static org.assertj.core.api.Assertions.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.bwaldvogel.mongo.*;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.integration.school.model.Student;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.example.integration.school.repository.StudentMongoRepository.*;

public class StudentMongoRepositoryTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		client = new MongoClient(new ServerAddress(serverAddress));
		studentRepository = new StudentMongoRepository(client);
		MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
		database.drop();
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}

	@Test
	public void test_findAll_when_database_is_empty() {
		assertThat(studentRepository.findAll()).isEmpty();
	}

	@Test
	public void test_findAll_when_DB_is_not_empty() {
		addTestStudentToDB("1", "test1");
		addTestStudentToDB("2", "test2");
		assertThat(studentRepository.findAll()).containsExactly(new Student("1", "test1"), new Student("2", "test2"));
	}

	private void addTestStudentToDB(String id, String name) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name));
	}

	@Test
	public void test_findById_not_found() {
		assertThat(studentRepository.findById("1")).isNull();
	}

	@Test
	public void test_findById_found() {
		addTestStudentToDB("1", "test1");
		addTestStudentToDB("2", "test2");
		assertThat(studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
	}

	@Test
	public void test_save() {
		Student student = new Student("1", "added student");
		studentRepository.save(student);
		assertThat(readAllStudentsFromDB()).containsExactly(student);
	}

	private List<Student> readAllStudentsFromDB() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(d.get("id").toString(), d.get("name").toString())).collect(Collectors.toList());
	}
	
	@Test
	public void test_delete() {
		addTestStudentToDB("1", "test1");
		studentRepository.delete("1");
		assertThat(readAllStudentsFromDB()).isEmpty();
	}

}
