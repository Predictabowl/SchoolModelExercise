package com.example.integration.school.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

import com.example.integration.school.model.Student;
import com.example.integration.school.repository.*;
import com.example.integration.school.view.StudentView;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import org.assertj.swing.timing.*;
import org.bson.Document;

import static org.awaitility.Awaitility.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SchoolControllerRaceConditionIT {

	private static final int THREADS_NUM = 15;
	private StudentRepository studentRepository;

	@Mock
	private StudentView studentView;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		MongoClient client = new MongoClient("localhost");
		MongoDatabase database = client.getDatabase(StudentMongoRepository.SCHOOL_DB_NAME);
		database.drop();
		MongoCollection<Document> studenCollection = database
				.getCollection(StudentMongoRepository.STUDENT_COLLECTION_NAME);
		studenCollection.createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
		studentRepository = new StudentMongoRepository(client);
	}

	@Test
	public void test_newStudent_Concurrent() {
		Student student = new Student("1", "Mario");
		ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUM);

		List<Thread> threads = IntStream.range(0, THREADS_NUM)
				.mapToObj(
						i -> new Thread(() -> new SchoolController(studentRepository, studentView).newStudent(student)))
				.peek(t -> t.start()).collect(Collectors.toList());

		await().atMost(5, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		assertThat(studentRepository.findAll()).containsExactly(student);
		executor.shutdown();
	}

}
