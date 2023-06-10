Feature: BankAccounts CRUD operations

  Scenario: Getting all bank accounts
    Given The endpoint for "cars" is available for method "GET"
    When I retrieve all cars
    Then I should receive all cars

  Scenario: Create bank account
    Given The endpoint for "cars" is available for method "POST"
    When I create a car with brand "Toyota" and license plate "CD4567" with weight 1600 and owner id 1
    Then The response status is 201
    And The car ID is 2