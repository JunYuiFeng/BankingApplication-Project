Feature: Transaction CRUD

  Scenario: make a transaction as customer
    Given the endpoint "Transactions" is available for method "POST"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43ABNA5253446745" description "test"
    Then the transaction response should have status code 201

    Scenario: make a transaction as customer with invalid accountFrom
    Given the endpoint "Transactions" is available for method "POST"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO366708600" accountTo "NL43ABNA5253446745" description "test"
    Then the transaction response should have status code 404

  Scenario: make a transaction as customer with invalid accountTo
    Given the endpoint "Transactions" is available for method "POST"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43ABNA525344674" description "test"
    Then the transaction response should have status code 404

  Scenario: make a transaction as a customer to a savings you dont own
    Given the endpoint "Transactions" is available for method "POST"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43ABNA5253446746" description "test"
    Then the transaction response should have status code 403

  Scenario: make a transaction as a customer to a savings you own
    Given the endpoint "Transactions" is available for method "POST"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43RABO5553446746" description "test"
    Then the transaction response should have status code 201

