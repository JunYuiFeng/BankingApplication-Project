Feature: UserAccounts CRUD

  Scenario: Get all userAccounts
    Given When the endpoint "UserAccounts" is available for method "GET"
    When I retrieve all UserAccounts
    Then the response should have status code 200
    And I should have a list of 6 UserAccounts

  Scenario: Get a userAccount by id
    Given When the endpoint "UserAccounts" is available for method "GET"
    When I retrieve a UserAccount with id 1
    Then the response should have status code 200
    And I should have a UserAccount with id 1

  Scenario: Get a userAccount by username
    Given When the endpoint "UserAccounts" is available for method "GET"
    When I retrieve a UserAccount with username "JohnDoe"
    Then the response should have status code 200
    And I should have a UserAccount with username "JohnDoe"

  Scenario: Create a new userAccount
    Given When the endpoint "UserAccounts/register" is available for method "POST"
    When I create a new UserAccount
    Then the response should have status code 200
    And I should have a UserAccount with id 7

  Scenario: Update a userAccount
    Given When the endpoint "UserAccounts/2" is available for method "PUT"
    When I update a UserAccount with id 2
    Then the response should have status code 200

  Scenario: Delete a userAccount
    Given When the endpoint "UserAccounts/5" is available for method "DELETE"
    When I delete a UserAccount with id 5
    Then the response should have status code 200

  #Patch is not supported by the random port assigned while cucumber testing
#  Scenario: Patch a userAccount
#    Given When the endpoint "UserAccounts/2" is available for method "PATCH"
#    When I patch a UserAccount with id 2 with status "INACTIVE"
#    Then the response should have status code 200
#    And I should have a UserAccount with id 2 and status "INACTIVE"

