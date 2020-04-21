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
Feature: Student Application Frame
  Specification of the behaviour of the Student Application Frame

  @tag1
  Scenario: The initial state of the view
    Given The database contains the students with the following values
    	| 1 | first student  |
    	| 2 | second student |
    When The student view is shown
    Then The list contains the elements in the following table
      | 1 | first student  |
    	| 2 | second student |
    	
	Scenario: Add a new student
		Given The student view is shown
		When The user enter the following values in the text fields
			| id | name 				 |
			| 1  | a new student |
		And The user click the "Add" button
		Then The list contains the elements in the following table
		  | 1 | a new student |
		  
	Scenario: Add a new student with an existing id
		Given The database contains the students with the following values
			| 1 | Carlo Pedersoli |
		And The student view is shown
		When The user enter the following values in the text fields
			| id | name          |
			| 1  | Mario Girotti |
		And The user click the "Add" button
		Then An error is shown containing the following values
		  | 1  | Carlo Pedersoli |
		  
	Scenario: Remove a student
		Given The database contains the students with the following values
    	| 1 | first student  |
    	| 2 | second student |
    And The student view is shown
    When The user select the row 1 within the student list
    And The user click the "Delete Selected" button
    Then The list contains the elements in the following table
		  | 2 | second student |


    