package com.example.integration.school.unitofwork;

import com.example.integration.school.repository.StudentRepository;

public interface UnitOfWork {
	public StudentRepository getRepository();
	public void commit();
}
