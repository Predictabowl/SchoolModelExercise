package com.example.integration.school.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.integration.school.model.Student;
import com.example.integration.school.repository.StudentRepository;
import com.example.integration.school.view.StudentView;

public class SchoolControllerTest {

	@Mock
	private StudentRepository studentRepository;
	
	@Mock
	private StudentView studentView;

	private SchoolController schoolController;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		schoolController = new SchoolController(studentRepository,studentView);
	}
	
	@Test
	public void test_all_students() {
		List<Student> students = asList(new Student());
		when(studentRepository.findAll()).thenReturn(students);
		schoolController.allStudents();
		verify(studentView).showAllStudents(students);
	}
	
	@Test
	public void test_new_student_when_it_does_not_exists_yet() {
		Student student = new Student("1","test");
		when(studentRepository.findById("1")).thenReturn(null);
		schoolController.newStudent(student);	
		InOrder inOrder = inOrder(studentRepository,studentView);
		inOrder.verify(studentRepository).save(student);
		inOrder.verify(studentView).studentAdded(student);
	}
	
	@Test
	public void test_new_student_when_it_alterady_exists() {
		Student studentToAdd = new Student("1","test");
		Student existingStudent = new Student("1","Mario");
		when(studentRepository.findById("1")).thenReturn(existingStudent);
		schoolController.newStudent(studentToAdd);
		verify(studentView).showError("Already existing student with id 1", existingStudent);
		verifyNoMoreInteractions(ignoreStubs(studentRepository));
	}
	
	@Test
	public void test_delete_student_when_it_exists() {
		Student studentToDelete = new Student("1","test");
		when(studentRepository.findById("1")).thenReturn(studentToDelete);
		schoolController.deleteStudent(studentToDelete);
		InOrder inOrder = inOrder(studentRepository,studentView);
		inOrder.verify(studentRepository).delete("1");
		inOrder.verify(studentView).studentRemove(studentToDelete);
	}
	
	@Test
	public void test_deleteStudent_when_Student_does_not_exists() {
		Student studentToDelete = new Student("1","test");
		when(studentRepository.findById("1")).thenReturn(null);
		schoolController.deleteStudent(studentToDelete);
		verify(studentView).showErrorStudentNotFound("No existing student with id 1", studentToDelete);
		verifyNoMoreInteractions(ignoreStubs(studentRepository));
	}
}
