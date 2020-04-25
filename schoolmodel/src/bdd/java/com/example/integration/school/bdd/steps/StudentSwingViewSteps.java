package com.example.integration.school.bdd.steps;

import static com.example.integration.school.view.StudentSwingView.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.util.Patterns;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StudentSwingViewSteps {
	private static final String FIXTURE_NEW_NAME = "new student";
	private static final String FIXTURE_NEW_ID = "10";
	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";

	private FrameFixture window;


	@After
	public void tearDown() {
		if (window != null)
			window.cleanUp();
	}

	@When("The student view is shown")
	public void the_student_view_is_shown() {
		application("com.example.integration.school.app.swing.SchoolSwingApp")
				.withArgs("--db-name=" + DB_NAME, "--db-collection=" + COLLECTION_NAME).start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

			@Override
			protected boolean isMatching(JFrame component) {
				return "Student View".equals(component.getTitle()) && component.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@Then("The list contains the elements in the following table")
	public void the_list_contains_the_elements_in_the_following_table(List<List<String>> dataTable) {
		dataTable.forEach(
				l -> assertThat(window.list().contents()).anySatisfy(r -> assertThat(r).contains(l.get(0), l.get(1))));
	}

	@When("The user enter the following values in the text fields")
	public void the_user_enter_the_following_values_in_the_text_fields(List<Map<String, String>> dataTable) {
		dataTable.forEach(c -> c.entrySet()
				.forEach(a -> window.textBox(a.getKey() + "TextBox").enterText(a.getValue())));
	}

//	@When("The user enter the following values in the text fields")
//	public void the_user_enter_the_following_values_in_the_text_fields(List<Map<String, String>> dataTable) {
//		dataTable.stream().flatMap(m -> m.entrySet().stream())
//				.forEach(e -> window.textBox(e.getKey() + "TextBox").enterText(e.getValue()));
//	}

	@When("The user click the {string} button")
	public void the_user_click_the_button(String button) {
		window.button(JButtonMatcher.withText(button)).click();
	}
	
	@Then("An error is shown containing the following values")
	public void an_error_is_shown_containing_the_following_values(List<List<String>> dataTable) {
		assertThat(window.label(ERROR_MESSAGE_LABEL).text()).contains(dataTable.get(0));
	}
	
	@When("The user select the row {int} within the student list")
	public void the_user_select_the_row_within_the_student_list(Integer index) {
		window.list(STUDENT_LIST).selectItem(index-1);
	}
	
	@Given("The user enter student data in the text field")
	public void the_user_enter_student_data_in_the_text_field() {
		window.textBox(ID_TEXT).enterText(FIXTURE_NEW_ID);
		window.textBox(NAME_TEXT).enterText(FIXTURE_NEW_NAME);
	}

	@Then("The list in the view contains the new student")
	public void the_list_in_the_view_contains_the_new_student() {
		assertThat(window.list().contents()).anySatisfy(c -> assertThat(c).contains(FIXTURE_NEW_ID,FIXTURE_NEW_NAME));
	}
	
	@Given("The user enter student data in the text field, specifying an existing id")
	public void the_user_enter_student_data_in_the_text_field_specifying_an_existing_id() {
		window.textBox(ID_TEXT).enterText(DatabaseSteps.FIXTURE_1_ID);
		window.textBox(NAME_TEXT).enterText(FIXTURE_NEW_NAME);
	}

	@Then("An error message will be shown containing the data of the existing student")
	public void an_error_message_will_be_shown_containing_the_data_of_the_existing_student() {
		assertThat(window.label(ERROR_MESSAGE_LABEL).text()).contains(DatabaseSteps.FIXTURE_1_ID,DatabaseSteps.FIXTURE_1_NAME);
	}
	
	@Given("The user select a student within the view list")
	public void the_user_select_a_student_within_the_view_list() {
		window.list(STUDENT_LIST).selectItems(Pattern.compile(".*"+DatabaseSteps.FIXTURE_1_NAME+".*"));
	}

	@Then("The student is removed from the list")
	public void the_student_is_removed_from_the_list() {
	    assertThat(window.list(STUDENT_LIST).contents()).noneMatch(p -> p.contains(DatabaseSteps.FIXTURE_1_NAME));
	}

	@Then("An error message will be shown containing the data of the selected student")
	public void an_error_message_will_be_shown_containing_the_data_of_the_selected_student() {
	    assertThat(window.label(ERROR_MESSAGE_LABEL).text()).contains(DatabaseSteps.FIXTURE_1_ID,DatabaseSteps.FIXTURE_1_NAME);
	}



	
}
