package com.example.integration.school.view;

import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
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

import com.example.integration.school.model.Student;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase{

	private static final String ERROR_MESSAGE_LABEL = "errorMessageLabel";
	private static final String STUDENT_LIST = "studentList";
	private static final String NAME_TEXT = "nameTextBox";
	private static final String ID_TEXT = "idTextBox";
	private static final String ADD_BUTTON = "Add";
	private static final String DELETE_BUTTON = "Delete Selected";
	private StudentSwingView studentSwingView;
	private FrameFixture window;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
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
		window.list(STUDENT_LIST).selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText(DELETE_BUTTON));
		deleteButton.requireEnabled();
		window.list(STUDENT_LIST).clearSelection();
		deleteButton.requireDisabled();
	}

}
