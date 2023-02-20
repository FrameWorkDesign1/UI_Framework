Feature: UI Testing


  @UI @sanity
    Scenario Outline: Testing UI
    Given Load properties from csv file "test_data/csv/Credential.CSV" with column "Cred" having value "<Example>"
    Given Set Api testing environment "#($OrangrHRM)"
    And Print property value for "#($OrangrHRM)"
    And Load properties from file "src/test/resources/config.properties"
    When Open browser with url
    Given User enter Username "#($Username)" and Password "#($Password)"

    #    Clicking on admin tab
    And Wait for 1 seconds
    And Click on sidebar navigation "Admin" and save "Admin"
    And Wait for 1 seconds
    And Validate Admin tab (System Users) and filled detailed "Test"
#    And Validate DropDown
    Then I get all the options from the dropdown

    Examples:
    | Example |
    |OrangeHRM|
