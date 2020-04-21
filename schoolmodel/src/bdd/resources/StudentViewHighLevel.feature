#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@tag
Feature: Student View High Level
	Specifications of the behaviour of the Student View
	
	Background:
		Given The database contains a few students
		And The student view is shown
		
	Scenario: Add a new student
		Given The user enter student data in the text field
		When The user click the "Add" button
		Then The list in the view contains the new student 

	Scenario: Add a new student with an existing id
		Given The user enter student data in the text field, specifying an existing id
		When The user click the "Add" button
		Then An error message will be shown containing the data of the existing student
		
	Scenario: Delete a student
		Given The user select a student within the view list
		When The user click the "Delete Selected" button
		Then The student is removed from the list
		
	Scenario: Delete a non-existing student 
		Given The user select a student within the view list
		But The student is in the meantime removed from the database
		When The user click the "Delete Selected" button
		Then An error message will be shown containing the data of the selected student
		And The student is removed from the list # new specification