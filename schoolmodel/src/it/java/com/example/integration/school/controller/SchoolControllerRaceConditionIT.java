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
import com.mongodb.client.MongoDatabase;

import org.assertj.swing.timing.*;
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
		studentRepository = new StudentMongoRepository(client);
	}

	@Test
	public void test_newStudent_Concurrent() {
		Student student = new Student("1", "Mario");
		ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUM);
		
		
		// imperative language is definitely more readable to me
		List<Future<?>> futures = new ArrayList<>(THREADS_NUM);
		for (int i = 0; i < THREADS_NUM; i++) {
			futures.add(executor.submit(() -> new SchoolController(studentRepository, studentView).newStudent(student)));
		}

		// functional version, too cluttered in this case. Slightly slower.
//		List<Future<?>> futures = IntStream.range(0, THREADS_NUM).
//				mapToObj(i -> executor.submit(() -> new SchoolController(studentRepository, studentView).newStudent(student))).
//				collect(Collectors.toList());
		
		// Standard assertj swing, no need of Awaitility.
		Pause.pause(new Condition("Wait for threads to terminate") {
			
			@Override
			public boolean test() {
				return futures.stream().allMatch(p -> p.isDone());
			}
			
		},Timeout.timeout(5, TimeUnit.SECONDS));

		//Awaitility, don't require anonymous class. Definetly slower.
//		await().atMost(5, TimeUnit.SECONDS).until(() -> futures.stream().allMatch(p -> p.isDone()));
		
		assertThat(studentRepository.findAll()).containsExactly(student);
		executor.shutdown();
	}

}
