package com.example.integration.school.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
//import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import com.example.integration.school.model.Student;
import com.example.integration.school.repository.StudentMongoRepository;
import com.example.integration.school.repository.StudentRepository;
import com.example.integration.school.view.StudentView;
import com.mongodb.MongoClient;

/*
 * When we make integration tests we focus on a single class?
 * For example in this case we test the integration between the repository and the controller,
 * but the test case is focused exclusively on the controller, 
 */

public class SchoolControllerIT {

	@Mock
	private StudentView studentView;
	
	private StudentRepository studentRepository;
	private SchoolController schoolController;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		studentRepository = new StudentMongoRepository(new MongoClient("localhost"));
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		schoolController = new SchoolController(studentRepository, studentView);
	}
	
	@Test
	public void test_allStudens() {
		Student student = new Student("1", "test1");
		studentRepository.save(student);
		schoolController.allStudents();
		verify(studentView).showAllStudents(Arrays.asList(student));
	}
	
	@Test
	public void test_newStudent() {
		Student student = new Student("1","test1");
		schoolController.newStudent(student);
		verify(studentView).studentAdded(student);
		// Why in the book the following assertion is not present? we assume is covered by unit tests?
//		assertThat(studentRepository.findById("1")).isEqualTo(student);
	}
	
	@Test
	public void test_deleteStudent() {
		Student student = new Student("1", "test1");
		studentRepository.save(student);
		schoolController.deleteStudent(student);
		verify(studentView).studentRemove(student);
	}
	

}
