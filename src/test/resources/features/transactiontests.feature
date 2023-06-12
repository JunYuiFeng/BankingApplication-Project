Feature: Transaction CRUD

  Scenario: make a transaction as customer
    Given the endpoint "Transactions" is available for method "POST" as "customer"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43ABNA5253446745" description "test"
    Then the transaction response should have status code 201

  Scenario: make a transaction as customer with invalid accountFrom
    Given the endpoint "Transactions" is available for method "POST" as "customer"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO366708600" accountTo "NL43ABNA5253446745" description "test"
    Then the transaction response should have status code 404

  Scenario: make a transaction as customer with invalid accountTo
    Given the endpoint "Transactions" is available for method "POST" as "customer"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43ABNA525344674" description "test"
    Then the transaction response should have status code 404

  Scenario: make a transaction as a customer to a savings you dont own
    Given the endpoint "Transactions" is available for method "POST" as "customer"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43ABNA5253446746" description "test"
    Then the transaction response should have status code 400

  Scenario: make a transaction as a customer to a savings you own
    Given the endpoint "Transactions" is available for method "POST" as "customer"
    When the customer makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43RABO5553446746" description "test"
    Then the transaction response should have status code 201

  Scenario: make a transaction as customer with savings you own to current you don't
    Given the endpoint "Transactions" is available for method "POST" as "customer"
    When the customer makes a transaction with amount 10 accountFrom "NL43RABO5553446746" accountTo "NL43ABNA5253446745" description "test"
    Then the transaction response should have status code 400

  Scenario: make a transaction as customer with savings you don't own to current you do
    Given the endpoint "Transactions" is available for method "POST" as "customer"
    When the customer makes a transaction with amount 10 accountFrom "NL43ABNA5253446746" accountTo "NL71RABO3667086008" description "test"
    Then the transaction response should have status code 400

  Scenario: make a transaction as employee for a customer
    Given the endpoint "Transactions" is available for method "POST" as "employee"
    When the employee makes a transaction with amount 10 accountFrom "NL71RABO3667086008" accountTo "NL43ABNA5253446745" description "test"
    Then the transaction response should have status code 201

  Scenario: customer deposits money to current
    Given the endpoint "Transactions/Deposit" is available for method "POST" as "customer"
    When the customer makes an atm request "Deposit" with IBAN "NL71RABO3667086008" and amount 10
    Then the transaction response should have status code 201

  Scenario: customer deposits money to savings
    Given the endpoint "Transactions/Deposit" is available for method "POST" as "customer"
    When the customer makes an atm request "Deposit" with IBAN "NL43RABO5553446746" and amount 10
    Then the transaction response should have status code 400

  Scenario: customer deposits money to current with invalid IBAN
    Given the endpoint "Transactions/Deposit" is available for method "POST" as "customer"
    When the customer makes an atm request "Deposit" with IBAN "NL71RABO366708600" and amount 10
    Then the transaction response should have status code 404

  Scenario: customer deposits money to current with invalid amount
    Given the endpoint "Transactions/Deposit" is available for method "POST" as "customer"
    When the customer makes an atm request "Deposit" with IBAN "NL71RABO3667086008" and amount -10
    Then the transaction response should have status code 400

  Scenario: customer withdraws money from current
    Given the endpoint "Transactions/Withdrawal" is available for method "POST" as "customer"
    When the customer makes an atm request "Withdrawal" with IBAN "NL71RABO3667086008" and amount 10
    Then the transaction response should have status code 201

  Scenario: customer withdraws money from savings
    Given the endpoint "Transactions/Withdrawal" is available for method "POST" as "customer"
    When the customer makes an atm request "Withdrawal" with IBAN "NL43RABO5553446746" and amount 10
    Then the transaction response should have status code 400

  Scenario: customer withdraws money from current with insufficient funds
    Given the endpoint "Transactions/Withdrawal" is available for method "POST" as "customer"
    When the customer makes an atm request "Withdrawal" with IBAN "NL71RABO3667086008" and amount 100000
    Then the transaction response should have status code 400
