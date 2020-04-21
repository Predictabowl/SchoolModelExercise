package com.example.integration.school.view;

import static com.example.integration.school.view.StudentSwingView.*;
import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.model.Student;
import com.example.integration.school.repository.StudentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class StudentSwingViewIT extends AssertJSwingJUnitTestCase {

	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String SCHOOL_DB_NAME = "school";
	
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	private MongoClient client;
	private StudentMongoRepository studentRespository;
	private StudentSwingView studentSwingView;
	private SchoolController schoolController;
	private FrameFixture window;

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		client = new MongoClient(new ServerAddress(serverAddress));
		studentRespository = new StudentMongoRepository(client,SCHOOL_DB_NAME,STUDENT_COLLECTION_NAME);
		for (Student student : studentRespository.findAll()) {
			studentRespository.delete(student.getId());
		}
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			schoolController = new SchoolController(studentRespository, studentSwingView);
			studentSwingView.setSchoolController(schoolController);
			return studentSwingView;
		});
		window = new FrameFixture(robot(), studentSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		client.close();
	}
	
	@Test @GUITest
	public void test_allStudents() {
		Student student = new Student("1", "Mario");
		Student	student2 = new Student("2", "Carlo");
		studentRespository.save(student);
		studentRespository.save(student2);
		GuiActionRunner.execute(() -> schoolController.allStudents());
		assertThat(window.list().contents()).containsExactly(student.toString(),student2.toString());
	}
	
	@Test @GUITest
	public void test_addButton_success() {
		window.textBox(ID_TEXT).enterText("1");
		window.textBox(NAME_TEXT).enterText("test1");
		window.button(JButtonMatcher.withText(ADD_BUTTON)).click();
		assertThat(window.list().contents()).containsExactly(new Student("1", "test1").toString());
	}
	
	@Test @GUITest
	public void test_addButton_error() {
		studentRespository.save(new Student("1", "test1"));
		window.textBox(ID_TEXT).enterText("1");
		window.textBox(NAME_TEXT).enterText("Mario");
		window.button(JButtonMatcher.withText(ADD_BUTTON)).click();
		assertThat(window.list().contents()).isEmpty();
		window.label(ERROR_MESSAGE_LABEL).requireText("Already existing student with id 1: "+new Student("1", "test1"));
	}
	
	@Test @GUITest
	public void test_deleteButton_success() {
		GuiActionRunner.execute(() -> schoolController.newStudent(new Student("1", "Condannato")));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText(DELETE_BUTTON)).click();
		assertThat(window.list().contents()).isEmpty();
	}
	
	@Test @GUITest
	public void test_deleteButton_error() {
		Student student = new Student("1", "test1");
		GuiActionRunner.execute(() -> studentSwingView.getListStudentModel().addElement(student));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText(DELETE_BUTTON)).click();
		window.label(ERROR_MESSAGE_LABEL).requireText("No existing student with id 1: "+student);
		assertThat(window.list().contents()).isEmpty();
	}
}
