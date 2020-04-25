package com.example.integration.school.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.example.integration.school.controller.SchoolController;
import com.example.integration.school.model.Student;

import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class StudentSwingView extends JFrame implements StudentView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ERROR_MESSAGE_LABEL = "errorMessageLabel";
	public static final String STUDENT_LIST = "studentList";
	public static final String NAME_TEXT = "nameTextBox";
	public static final String ID_TEXT = "idTextBox";
	public static final String ADD_BUTTON = "Add";
	public static final String DELETE_BUTTON = "Delete Selected";
	private JPanel contentPane;
	private JTextField txtId;
	private JTextField txtName;
	private JButton btnAdd;
	
	private JList<Student> listStudents;
	private DefaultListModel<Student> listStudentModel;
	private JButton btnDeleteSelected;
	private JLabel lblErrorMessage;

	private transient SchoolController schoolController;



	/**
	 * Create the frame.
	 */
	public StudentSwingView() {
		setTitle("Student View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 452, 309);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		makeIdLabel();

		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				btnAdd.setEnabled(!txtId.getText().trim().isEmpty() && !txtName.getText().trim().isEmpty());
			}
		};
		makeIdTextBox(btnAddEnabler);
		makeNameLabel();
		makeNameTextBox(btnAddEnabler);
		makeAddButton();
		makeStudentList();
		makeDeleteButton();
		makeErrorLabel();
	}

	private void clearErrorLabel() {
		lblErrorMessage.setText(" ");
	}

	DefaultListModel<Student> getListStudentModel() {
		return listStudentModel;
	}

	

	private void makeDeleteButton() {
		btnDeleteSelected = new JButton(DELETE_BUTTON);
		btnDeleteSelected.addActionListener(
				arg0 -> new Thread(() -> schoolController.deleteStudent(listStudents.getSelectedValue())).start());
		btnDeleteSelected.setEnabled(false);
		GridBagConstraints gbc_btnDeleteSelected = new GridBagConstraints();
		gbc_btnDeleteSelected.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteSelected.gridwidth = 2;
		gbc_btnDeleteSelected.gridx = 0;
		gbc_btnDeleteSelected.gridy = 4;
		contentPane.add(btnDeleteSelected, gbc_btnDeleteSelected);
		
	}

	private void makeErrorLabel() { 
		lblErrorMessage = new JLabel(" ");
		lblErrorMessage.setName(ERROR_MESSAGE_LABEL);
		GridBagConstraints gbc_lblErrorMessage = new GridBagConstraints();
		gbc_lblErrorMessage.gridx = 1;
		gbc_lblErrorMessage.gridy = 5;
		contentPane.add(lblErrorMessage, gbc_lblErrorMessage);
	}

	private void makeIdLabel() {
		JLabel lblId = new JLabel("id");
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.insets = new Insets(0, 0, 5, 5);
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 0;
		contentPane.add(lblId, gbc_lblId);
	}

	private void makeIdTextBox(KeyAdapter btnAddEnabler) {
		txtId = new JTextField();
		txtId.addKeyListener(btnAddEnabler);
		txtId.setName(ID_TEXT);
		GridBagConstraints gbc_txtId = new GridBagConstraints();
		gbc_txtId.insets = new Insets(0, 0, 5, 0);
		gbc_txtId.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtId.gridx = 1;
		gbc_txtId.gridy = 0;
		contentPane.add(txtId, gbc_txtId);
		txtId.setColumns(10);
	}

	private void makeNameLabel() {
		JLabel lblName = new JLabel("name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 1;
		contentPane.add(lblName, gbc_lblName);
	}

	private void makeNameTextBox(KeyListener btnAddEnabler) {
		txtName = new JTextField();
		txtName.addKeyListener(btnAddEnabler);
		txtName.setName(NAME_TEXT);
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.insets = new Insets(0, 0, 5, 0);
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.gridx = 1;
		gbc_txtName.gridy = 1;
		contentPane.add(txtName, gbc_txtName);
		txtName.setColumns(10);
	}

	private void makeAddButton() {
		btnAdd = new JButton(ADD_BUTTON);
		btnAdd.addActionListener(
				arg0 -> new Thread(() ->{
					schoolController.newStudent(new Student(txtId.getText(), txtName.getText()));	
					txtId.setText("");
					txtName.setText("");
				}).start());
		btnAdd.setEnabled(false);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.gridwidth = 2;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 2;
		contentPane.add(btnAdd, gbc_btnAdd);
	}
	
	private void makeStudentList() {
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		contentPane.add(scrollPane, gbc_scrollPane);

		listStudentModel = new DefaultListModel<>();
		listStudents = new JList<>(listStudentModel);
		listStudents
				.addListSelectionListener(arg0 -> btnDeleteSelected.setEnabled(listStudents.getSelectedIndex() != -1));
		scrollPane.setViewportView(listStudents);
		listStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listStudents.setName(STUDENT_LIST);
		listStudents.setCellRenderer(new DefaultListCellRenderer() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, getDisplayString((Student)value), index, isSelected, cellHasFocus);
			}
			
		});
	}

	public void setSchoolController(SchoolController schoolController) {
		this.schoolController = schoolController;
	}
	
	private String getDisplayString(Student student) {
		return student.getId()+" - "+student.getName();
	}

	@Override
	public void showAllStudents(List<Student> students) {
		SwingUtilities.invokeLater(() -> students.stream().forEach(listStudentModel::addElement));
	}

	@Override
	public void showError(String message, Student student) {
		SwingUtilities.invokeLater(() -> lblErrorMessage.setText(message + ": " + getDisplayString(student)));
	}

	@Override
	public void showErrorStudentNotFound(String message, Student student) {
		lblErrorMessage.setText(message+": "+getDisplayString(student));
		listStudentModel.removeElement(student);
	}

	@Override
	public void studentAdded(Student student) {
		SwingUtilities.invokeLater(() -> {
			listStudentModel.addElement(student);
			clearErrorLabel();
		});
	}

	@Override
	public void studentRemove(Student student) {
		SwingUtilities.invokeLater(() -> {
			listStudentModel.removeElement(student);
			clearErrorLabel();
		});
	}
}
