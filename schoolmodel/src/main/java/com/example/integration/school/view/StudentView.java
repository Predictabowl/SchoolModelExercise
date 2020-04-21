package com.example.integration.school.view;

import java.util.List;

import com.example.integration.school.model.Student;

public interface StudentView {
	void showAllStudents(List <Student> students);
	void showError(String message, Student student);
	void showErrorStudentNotFound(String message, Student student);
	void studentAdded(Student student);
	void studentRemove(Student student);

}
