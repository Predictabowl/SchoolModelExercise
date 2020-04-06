package com.example.integration.predictabowl.schoolmodel;

import java.util.List;

public interface StudentView {
	void showAllStudents(List <Student> students);
	void showError(String message, Student student);
	void studentAdded(Student student);
	void studentRemove(Student student);

}
