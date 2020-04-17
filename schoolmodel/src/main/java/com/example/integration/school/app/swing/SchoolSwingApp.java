package com.example.integration.school.app.swing;

import java.awt.EventQueue;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.repository.StudentMongoRepository;
import com.example.integration.school.view.StudentSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class SchoolSwingApp {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String mongoHost = "localhost";
					int mongoPort = 27017;
					if (args.length > 0)
						mongoHost = args[0];
					if (args.length > 1)
						mongoPort = Integer.parseInt(args[1]);
					StudentMongoRepository studentRepository = new StudentMongoRepository(
							new MongoClient(new ServerAddress(mongoHost, mongoPort)), "school", "student");
					StudentSwingView studentView = new StudentSwingView();
					SchoolController schoolController = new SchoolController(studentRepository, studentView);
					studentView.setSchoolController(schoolController);
					
					studentView.setVisible(true);
					schoolController.allStudents();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
