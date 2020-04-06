package com.example.integration.school.model;

import java.util.List;

public interface StudentRepository {
	public List<Student> findAll();
	public Student findById(String Id);
	public void save (Student student);
	public void delete (String id);
}
