package com.example.integration.school.controller;

import com.example.integration.school.model.Student;
import com.example.integration.school.repository.StudentRepository;
import com.example.integration.school.view.StudentView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SchoolController {

	private StudentRepository studentRepository;
	private StudentView studentView;

	@Inject
	public SchoolController(StudentRepository studentRepository,@Assisted StudentView studentView) {
		this.studentRepository = studentRepository;
		this.studentView = studentView;
	}

	public void allStudents() {
		studentView.showAllStudents(studentRepository.findAll());
	}

	public void newStudent(Student student) {
		if (studentRepository.findById(student.getId()) != null) {
			studentView.showError("Already existing student with id " + student.getId(), studentRepository.findById(student.getId()));
		} else {
			studentRepository.save(student);
			studentView.studentAdded(student);
		}
	}

	public void deleteStudent(Student student) {
		if (studentRepository.findById(student.getId()) == null) {
			studentView.showErrorStudentNotFound("No existing student with id "+student.getId(), student);
		} else {
			studentRepository.delete(student.getId());
			studentView.studentRemove(student);
		}
	}

}
