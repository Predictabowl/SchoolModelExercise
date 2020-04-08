package com.example.integration.school.controller;

//import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.integration.school.model.Student;
import com.example.integration.school.repository.StudentMongoRepository;
import com.example.integration.school.repository.StudentRepository;
import com.example.integration.school.view.StudentView;
import com.mongodb.MongoClient;

 
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
