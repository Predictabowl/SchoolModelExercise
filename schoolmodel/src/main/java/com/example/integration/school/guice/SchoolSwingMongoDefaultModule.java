package com.example.integration.school.guice;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.repository.StudentMongoRepository;
import com.example.integration.school.repository.StudentRepository;
import com.example.integration.school.view.StudentSwingView;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mongodb.MongoClient;

public class SchoolSwingMongoDefaultModule extends AbstractModule {

	private String mongoHost = "localhost";
	private int mongoPort = 27017;
	private String dbName = "school";
	private String collectionName;
	
	public SchoolSwingMongoDefaultModule mongoHost(String mongoHost) {
		this.mongoHost = mongoHost;
		return this;
	}
	
	public SchoolSwingMongoDefaultModule mongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
		return this;
	}
	
	public SchoolSwingMongoDefaultModule databaseName(String dbName) {
		this.dbName = dbName;
		return this;
	}
	
	public SchoolSwingMongoDefaultModule collectionName (String collectionName) {
		this.collectionName = collectionName;
		return this;
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(MongoHost.class).toInstance(mongoHost);
		bind(Integer.class).annotatedWith(MongoPort.class).toInstance(mongoPort);
		bind(String.class).annotatedWith(MongoDbName.class).toInstance(dbName);
		bind(String.class).annotatedWith(MongoCollectionName.class).toInstance(collectionName);
		bind(StudentRepository.class).to(StudentMongoRepository.class);
		install(new FactoryModuleBuilder().implement(SchoolController.class, SchoolController.class)
				.build(SchoolControllerFactory.class));
	}
	
	@Provides
	StudentSwingView buildView(SchoolControllerFactory controllerFactory) {
		StudentSwingView view = new StudentSwingView();
		view.setSchoolController(controllerFactory.create(view));
		return view;
	}
	
	@Provides
	MongoClient buildMongoClient(@MongoHost String host, @MongoPort int port) {
		return new MongoClient(host, port);
	}
	

}
