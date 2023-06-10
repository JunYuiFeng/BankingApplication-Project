Feature: UserAccounts

  Scenario: Get all UserAccounts
    Given When the endpoint "UserAccounts" is available for method "GET"
    When I retrieve all UserAccounts
    Then the response should have status code 200
    And I should have a list of 3 UserAccounts