package com.example.integration.school.view;

import static org.assertj.swing.launcher.ApplicationLauncher.*;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class SchoolSwingE2E extends AssertJSwingJUnitTestCase {

	private static final String FIXTURE_2_NAME = "Mario Girotti";
	private static final String FIXTURE_2_ID = "2";
	private static final String FIXTURE_1_NAME = "Carlo Pedersoli";
	private static final String FIXTURE_1_ID = "1";
	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";
	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;

	private MongoClient mongoClient;
	private FrameFixture window;

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
		mongoClient.getDatabase(DB_NAME).drop();
		addTestStudentToDatabase(FIXTURE_1_ID, FIXTURE_1_NAME);
		addTestStudentToDatabase(FIXTURE_2_ID, FIXTURE_2_NAME);
		application("com.example.integration.school.app.swing.SchoolSwingApp").withArgs("--mongo-host=" + MONGO_HOST,
				"--mongo-port=" + MONGO_PORT, "--db-name=" + DB_NAME, "--db-collection=" + COLLECTION_NAME).start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

			@Override
			protected boolean isMatching(JFrame component) {
				return "Student View".equals(component.getTitle()) && component.isShowing();
			}
		}).using(robot());
	}

	private void addTestStudentToDatabase(String id, String name) {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name));
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void test_on_start_all_database_elements_are_shown() {
		assertThat(window.list().contents()).anySatisfy(e -> assertThat(e).contains(FIXTURE_1_ID, FIXTURE_1_NAME))
				.anySatisfy(e -> assertThat(e).contains(FIXTURE_2_ID, FIXTURE_2_NAME));
	}

	@Test
	@GUITest
	public void test_addButton_success() {
		window.textBox("idTextBox").enterText("10");
		window.textBox("nameTextBox").enterText("another student");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).anySatisfy(e -> assertThat(e).contains("10", "another student"));
	}
	
	@Test @GUITest
	public void test_addButton_failure() {
		window.textBox("idTextBox").enterText(FIXTURE_1_ID);
		window.textBox("nameTextBox").enterText("new one");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text()).contains(FIXTURE_1_ID,FIXTURE_1_NAME);
	}
	
	@Test @GUITest
	public void test_deleteButton_success() {
		window.list("studentList").selectItem(Pattern.compile(".*"+FIXTURE_1_NAME+".*"));
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list().contents()).noneMatch(e -> e.contains(FIXTURE_1_NAME));
	}
	
	@Test @GUITest
	public void test_deleteButton_failure() {
		window.list("studentList").selectItem(Pattern.compile(".*"+FIXTURE_1_NAME+".*"));
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).deleteOne(Filters.eq("id", FIXTURE_1_ID));
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.label("errorMessageLabel").text()).contains(FIXTURE_1_ID,FIXTURE_1_NAME);
	}

}
