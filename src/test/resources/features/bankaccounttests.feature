Feature: BankAccounts CRUD operations

  Scenario: Getting all bank accounts
    Given The endpoint for "BankAccounts" is available for method "GET"
    When I retrieve All BankAccounts
    Then I should receive all BankAccounts

  Scenario: Create bank account
    Given The endpoint for "BankAccounts" is available for method "POST"
    When I create a BankAccounts with type "CURRENT" and userId 2
    Then The response status is 201
    And The userId is 2

  Scenario: Update bank account
    Given The endpoint for "BankAccounts/{IBAN}" is available for method "PATCH"
    When I update a BankAccount with IBAN "NL71RABO3667086008" to status "ACTIVE"
    Then The response status is 201
    And The status is "ACTIVE"
