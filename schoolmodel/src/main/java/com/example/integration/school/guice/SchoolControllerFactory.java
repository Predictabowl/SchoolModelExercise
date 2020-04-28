package com.example.integration.school.guice;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.view.StudentView;

public interface SchoolControllerFactory {
	SchoolController create(StudentView view);
}
