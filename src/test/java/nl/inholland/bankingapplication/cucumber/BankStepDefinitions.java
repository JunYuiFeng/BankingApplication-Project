package nl.inholland.bankingapplication.cucumber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

public class BankStepDefinitions extends BaseStepDefinitions{
    @Autowired
    private TestRestTemplate restTemplate;
}
