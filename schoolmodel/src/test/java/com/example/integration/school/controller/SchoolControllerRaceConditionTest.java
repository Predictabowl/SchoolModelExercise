package com.example.integration.school.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.awaitility.Awaitility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.integration.school.model.Student;
import com.example.integration.school.repository.StudentRepository;
import com.example.integration.school.view.StudentView;

public class SchoolControllerRaceConditionTest {

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private StudentView studentView;

	private SchoolController schoolController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		schoolController = new SchoolController(studentRepository, studentView);
	}

	@Test
	public void test_newStudent_cuncurrent() {
		List<Student> studentList = new ArrayList<Student>();
		Student student = new Student("1", "test");
		when(studentRepository.findById(anyString()))
				.thenAnswer(invocation -> studentList.stream().findFirst().orElse(null));
		doAnswer(invocation -> {
			studentList.add(student);
			return null;
		}).when(studentRepository).save(any(Student.class));
		// threads
		int k = 20;
		ExecutorService executor = Executors.newFixedThreadPool(k);
		
		// stream version. It's messy and not much clear to read. 
//		List<Future<?>> futureList = IntStream.range(0, k)
//				.mapToObj(i -> executor.submit(() -> schoolController.newStudent(student)))
//				.collect(Collectors.toList());

		// honestly, the for loop is a lot more readable in this situation
		List<Future<?>> futureList = new ArrayList<>();
		for (int i=0; i<k;i++) {
			futureList.add(executor.submit(() -> schoolController.newStudent(student)));
		}

		await().atMost(10, TimeUnit.SECONDS).until(() -> futureList.stream().allMatch(f -> f.isDone()));
		assertThat(studentList).containsExactly(student);
		executor.shutdown();
	}

}
