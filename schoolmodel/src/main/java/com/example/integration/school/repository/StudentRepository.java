package com.example.integration.school.repository;

import java.util.List;

import com.example.integration.school.model.Student;

public interface StudentRepository {
	public List<Student> findAll();
	public Student findById(String Id);
	public void save (Student student);
	public void delete (String id);
}
