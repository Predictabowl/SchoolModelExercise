package com.example.integration.school.view;

import static com.example.integration.school.view.StudentSwingView.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

import java.util.concurrent.TimeUnit;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.model.Student;
import com.example.integration.school.repository.StudentMongoRepository;
import com.example.integration.school.view.StudentSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {

	private static final int TIMEOUT_SEC = 5;
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3").withExposedPorts(27017);
	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private FrameFixture window;
	private StudentSwingView studentSwingView;
	private SchoolController schoolController;

	@Override
	protected void onSetUp() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentRepository = new StudentMongoRepository(client);
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			schoolController = new SchoolController(studentRepository, studentSwingView);
			studentSwingView.setSchoolController(schoolController);
			return studentSwingView;
		}));
		window.show();
	}

	@Override
	protected void onTearDown() {
		client.close();
	}

	@Test
	public void test_addStudent() {
		window.textBox(ID_TEXT).enterText("1");
		window.textBox(NAME_TEXT).enterText("test");
		window.button(JButtonMatcher.withText(ADD_BUTTON)).click();
		await().atMost(TIMEOUT_SEC, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(studentRepository.findById("1")).isEqualTo(new Student("1", "test")));
	}

	@Test
	public void test_deleteStudent() {
		studentRepository.save(new Student("99", "Mario"));
		schoolController.allStudents();
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText(DELETE_BUTTON)).click();
		await().atMost(TIMEOUT_SEC, TimeUnit.SECONDS)
				.untilAsserted(() -> assertThat(studentRepository.findById("99")).isNull());
	}

}
