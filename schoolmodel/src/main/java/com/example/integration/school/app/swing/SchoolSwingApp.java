package com.example.integration.school.app.swing;

import java.awt.EventQueue;
import org.apache.logging.log4j.*;
import java.util.concurrent.Callable;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.guice.SchoolSwingMongoDefaultModule;
import com.example.integration.school.repository.StudentMongoRepository;
import com.example.integration.school.view.StudentSwingView;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class SchoolSwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = { "MongoDB host address" })
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = { "MongoDB host port" })
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = { "Database name" })
	private String databaseName = "school";

	@Option(names = { "--db-collection" }, description = { "Collection name" })
	private String collectionName = "student";

	public static final Logger logger = LogManager.getLogger(SchoolSwingApp.class);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new CommandLine(new SchoolSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				Module module = new SchoolSwingMongoDefaultModule()
						.mongoHost(mongoHost)
						.mongoPort(mongoPort)
						.databaseName(databaseName)
						.collectionName(collectionName);
				Guice.createInjector(module).getInstance(StudentSwingView.class).start();

//				StudentMongoRepository studentRepository = new StudentMongoRepository(
//						new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionName);
//				StudentSwingView studentView = new StudentSwingView();
//				SchoolController schoolController = new SchoolController(studentRepository, studentView);
//				studentView.setSchoolController(schoolController);
//
//				studentView.setVisible(true);
//				schoolController.allStudents();
			} catch (Exception e) {
				logger.fatal("Exception (test)", e);
			}
		});
		return null;
	}

}
