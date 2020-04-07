package com.example.integration.school.view;

import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase {

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

	@Test	@GUITest
	public void test_controls_initial_state() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox("idTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("name"));
		window.textBox("nameTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.list("studentList");
		window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test @GUITest
	public void test_when_id_and_name_are_not_empty_then_Add_button_should_be_enabled() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}
	
	@Test @GUITest
	public void test_when_either_ed_or_name_are_blank_then_Add_button_should_be_disabled() {
		JTextComponentFixture idTextBox = window.textBox("idTextBox");
		JTextComponentFixture nameTextBox = window.textBox("nameTextBox");
		
		idTextBox.enterText("1");
		nameTextBox.enterText(" ");
		JButtonMatcher jBMWithText = JButtonMatcher.withText("Add");
		window.button(jBMWithText).requireDisabled();
		
		idTextBox.setText("");
		nameTextBox.setText("");
		
		idTextBox.enterText(" ");
		nameTextBox.enterText("test");
		window.button(jBMWithText).requireDisabled();
	}

}
