package nl.inholland.bankingapplication.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.With;
import nl.inholland.bankingapplication.models.dto.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransactionStepDefinitions extends BaseStepDefinitions{
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private ResponseEntity<TransactionResponseDTO> response;

    private HttpHeaders headers = new HttpHeaders();
    @Given("the endpoint {string} is available for method {string}")
    public void theEndpointIsAvailableForMethod(String arg0, String arg1) {

    LoginDTO loginDTO = new LoginDTO("JunFeng", "secret123");
    ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity("/login", loginDTO, LoginResponseDTO.class);
    String token = loginResponse.getBody().token();

        headers.setBearerAuth(token);

    ResponseEntity<String> response = restTemplate.exchange("/" + arg0 , HttpMethod.OPTIONS, new HttpEntity<>(null, headers), String.class);
    List<String> options = Arrays.stream(response.getHeaders()
                    .get("Allow")
                    .get(0)
                    .split(","))
            .toList();
        Assertions.assertTrue(options.contains(arg1.toUpperCase()));
    }

    @When("the customer makes a transaction with amount {int} accountFrom {string} accountTo {string} description {string}")
    public void theCustomerMakesATransactionWithAmountAccountFromAccountToDescription(int arg0, String arg1, String arg2, String arg3) {
        response = restTemplate.exchange("/Transactions", HttpMethod.POST, new HttpEntity<>(new MakeTransactionDTO(arg1,arg2,arg0,arg3), headers), TransactionResponseDTO.class);
    }

    @Then("the transaction response should have status code {int}")
    public void theTransactionResponseShouldHaveStatusCode(int arg0) {
        Assertions.assertEquals(arg0, response.getStatusCode().value());
    }

    @When("the employee makes a transaction with amount {int} accountFrom {string} accountTo {string} description {string}")
    public void theEmployeeMakesATransactionWithAmountAccountFromAccountToDescription(int arg0, String arg1, String arg2, String arg3) {
        response = restTemplate.exchange("/Transactions", HttpMethod.POST, new HttpEntity<>(new MakeTransactionDTO(arg1,arg2,arg0,arg3), headers), TransactionResponseDTO.class);
    }

    @Given("the endpoint {string} is available for method {string} as {string}")
    public void theEndpointIsAvailableForMethodAs(String arg0, String arg1, String arg2) {
        String token = "";
        if (arg2.equals("customer")){
            LoginDTO loginDTO = new LoginDTO("JunFeng", "secret123");
            ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity("/login", loginDTO, LoginResponseDTO.class);
            token = loginResponse.getBody().token();
        }
        else if (arg2.equals("employee")){
            LoginDTO loginDTO = new LoginDTO("KarenWinter", "secret123");
            ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity("/login", loginDTO, LoginResponseDTO.class);
            token = loginResponse.getBody().token();
        }


        headers.setBearerAuth(token);

        ResponseEntity<String> response = restTemplate.exchange("/" + arg0 , HttpMethod.OPTIONS, new HttpEntity<>(null, headers), String.class);
        List<String> options = Arrays.stream(response.getHeaders()
                        .get("Allow")
                        .get(0)
                        .split(","))
                .toList();
        Assertions.assertTrue(options.contains(arg1.toUpperCase()));
    }

    @When("the customer makes an atm request {string} with IBAN {string} and amount {int}")
    public void theCustomerMakesAnAtmRequestWithIBANAndAmount(String arg0, String arg1, double arg2) {
        response = restTemplate.exchange("/Transactions/" + arg0, HttpMethod.POST, new HttpEntity<>(new WithdrawalAndDepositRequestDTO(arg1,arg2), headers), TransactionResponseDTO.class);
    }
}
