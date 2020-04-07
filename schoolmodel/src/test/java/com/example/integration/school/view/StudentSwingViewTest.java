package com.example.integration.school.view;

import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.model.Student;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final String ERROR_MESSAGE_LABEL = "errorMessageLabel";
	private static final String STUDENT_LIST = "studentList";
	private static final String NAME_TEXT = "nameTextBox";
	private static final String ID_TEXT = "idTextBox";
	private static final String ADD_BUTTON = "Add";
	private static final String DELETE_BUTTON = "Delete Selected";
	private StudentSwingView studentSwingView;
	private FrameFixture window;
	private SchoolController schoolController;

	/*
	 * the method robot() is from AssertJSwingTestCaseTemplate, which is imported by
	 * AssertjSwingJUnitTestCase so that's why can be seen.
	 */
	@Override
	protected void onSetUp() {
		schoolController = mock(SchoolController.class);
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			studentSwingView.setSchoolController(schoolController);
			return studentSwingView;
		});
		window = new FrameFixture(robot(), studentSwingView);
		window.show(); // Without it the test can't see the frame
	}

	@Test
	@GUITest
	public void test_controls_initial_state() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox(ID_TEXT).requireEnabled();
		window.label(JLabelMatcher.withText("name"));
		window.textBox(NAME_TEXT).requireEnabled();
		window.button(JButtonMatcher.withText(ADD_BUTTON)).requireDisabled();
		window.list(STUDENT_LIST);
		window.button(JButtonMatcher.withText(DELETE_BUTTON)).requireDisabled();
		window.label(ERROR_MESSAGE_LABEL).requireText(" ");
	}

	@Test
	@GUITest
	public void test_when_id_and_name_are_not_empty_then_Add_button_should_be_enabled() {
		window.textBox(ID_TEXT).enterText("1");
		window.textBox(NAME_TEXT).enterText("test");
		window.button(JButtonMatcher.withText(ADD_BUTTON)).requireEnabled();
	}

	@Test
	@GUITest
	public void test_when_either_ed_or_name_are_blank_then_Add_button_should_be_disabled() {
		JTextComponentFixture idTextBox = window.textBox(ID_TEXT);
		JTextComponentFixture nameTextBox = window.textBox(NAME_TEXT);

		idTextBox.enterText("1");
		nameTextBox.enterText(" ");
		JButtonMatcher jBMWithText = JButtonMatcher.withText(ADD_BUTTON);
		window.button(jBMWithText).requireDisabled();

		idTextBox.setText("");
		nameTextBox.setText("");

		idTextBox.enterText(" ");
		nameTextBox.enterText("test");
		window.button(jBMWithText).requireDisabled();
	}

	@Test
	@GUITest
	public void test_DeleteButton_shoulde_be_enabled_only_when_a_student_is_selected() {
		GuiActionRunner.execute(() -> studentSwingView.getListStudentModel().addElement(new Student("1", "test")));
		window.list(STUDENT_LIST).selectItem(0); // There's a single list but we need to find it with a name otherwise
													// it doesn't work
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText(DELETE_BUTTON));
		deleteButton.requireEnabled();
		window.list(STUDENT_LIST).clearSelection();
		deleteButton.requireDisabled();
	}

	@Test
	@GUITest
	public void test_showAllStudents_should_add_student_descriptions_to_the_list() {
		Student student1 = new Student("1", "test1");
		Student student2 = new Student("2", "test2");
		GuiActionRunner.execute(() -> studentSwingView.showAllStudents(Arrays.asList(student1, student2)));
		String[] listContents = window.list().contents(); // why here we don't need a name to find the list?
		assertThat(listContents).containsExactly(student1.toString(), student2.toString());
	}

	@Test
	@GUITest
	public void test_showError_should_show_the_message_in_the_error_label() {
		Student student = new Student("1", "test");
		GuiActionRunner.execute(() -> studentSwingView.showError("error message", student));
		window.label(ERROR_MESSAGE_LABEL).requireText("error message: " + student);
	}

	@Test
	@GUITest
	public void test_studentAdded_should_add_the_student_to_the_list_and_clear_the_error() {
		GuiActionRunner.execute(() -> studentSwingView.studentAdded(new Student("1", "test")));
		String[] listContent = window.list().contents();
		assertThat(listContent).containsExactly(new Student("1", "test").toString());
		window.label(ERROR_MESSAGE_LABEL).requireText(" ");
	}

	@Test
	@GUITest
	public void test_studentRemove_should_remove_the_student_from_list_and_clear_the_error() {
		// setup
		GuiActionRunner.execute(() -> {
			DefaultListModel<Student> listStudentModel = studentSwingView.getListStudentModel();
			listStudentModel.addElement(new Student("1", "test1"));
			listStudentModel.addElement(new Student("2", "test2"));
		});
		// execute
		GuiActionRunner.execute(() -> studentSwingView.studentRemove(new Student("1", "test1")));
		// verify
		String[] listContent = window.list().contents();
		assertThat(listContent).containsExactly(new Student("2", "test2").toString());
		window.label(ERROR_MESSAGE_LABEL).requireText(" ");
	}

	@Test
	public void test_AddButton_should_delegate_to_SchoolController_newStudent() {
		window.textBox(ID_TEXT).enterText("1");
		window.textBox(NAME_TEXT).enterText("test");
		window.button(JButtonMatcher.withText(ADD_BUTTON)).click();
		verify(schoolController).newStudent(new Student("1", "test"));
	}

	@Test
	public void test_DeleteButton_should_delegate_to_SchoolController_deleteStudent() {
		Student student1 = new Student("1", "test1");
		Student student2 = new Student("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Student> listStudentModel = studentSwingView.getListStudentModel();
			listStudentModel.addElement(student1);
			listStudentModel.addElement(student2);
		});
		window.list(STUDENT_LIST).selectItem(1);
		window.button(JButtonMatcher.withText(DELETE_BUTTON)).click();
		verify(schoolController).deleteStudent(student2);
	}
}
