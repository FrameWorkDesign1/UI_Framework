Feature: UI Testing
  
  @UI @sanity
  Scenario Outline: Testing UI
#    And Load properties from file "test_data/csv/Credential.CSV"
    Given Load properties from csv file "test_data/csv/Credential.CSV" with column "Cred" having value "<Example>"
    Given Set Api testing environment "#($OrangrHRM)"
    And Print property value for "#($OrangrHRM)"
    And Load properties from file "src/test/resources/config.properties"
    When Open browser with url
    Given User enter Username "#($Username)" and Password "#($Password)"

#    Clicking on admin tab
    And Wait for 1 seconds
    And Click on sidebar navigation "Admin" and save "Admin"
#

    And Wait for 1 seconds
    And Click on sidebar navigation "PIM" and save "PIM"


    And Wait for 1 seconds
    And Click on sidebar navigation "Leaves" and save "Admin"


    And Wait for 1 seconds
    And Click on sidebar navigation "Time" and save "Admin"


    And Wait for 1 seconds
    And Click on sidebar navigation "Recruitment" and save "Admin"


    And Wait for 1 seconds
    And Click on sidebar navigation "Performance" and save "Admin"


    And Wait for 1 seconds
    And Click on sidebar navigation "MyInfo" and save "Admin"


    And Wait for 1 seconds
    And Click on sidebar navigation "Dashboard" and save "Admin"


    And Wait for 1 seconds
    And Click on sidebar navigation "Directory" and save "Directory"


#    And Wait for 1 seconds
#    And Click on sidebar navigation "Maintenance" and save "Maintenance"
#    And Print property value for "Maintenance"

    And Wait for 1 seconds
    And Click on sidebar navigation "Buzz" and save "Admin"

    Examples:
      | Example |
      |OrangeHRM|

  