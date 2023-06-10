package nl.inholland.bankingapplication.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.bankingapplication.models.dto.BankAccountResponseDTO;
import nl.inholland.bankingapplication.models.dto.LoginDTO;
import nl.inholland.bankingapplication.models.dto.LoginResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import com.jayway.jsonpath.JsonPath;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

public class BankAccountsStepDefinitions extends BaseStepDefinitions {
    @Autowired
    private TestRestTemplate restTemplate; //provides a webclient
    private ResponseEntity<List<BankAccountResponseDTO>> bankAccountsResponse;
    private ResponseEntity<BankAccountResponseDTO> bankAccountResponse;


    HttpHeaders httpHeaders;

    private ResponseEntity<String> response;
    @Autowired
    private ObjectMapper mapper;

    public BankAccountsStepDefinitions() {
        httpHeaders = new HttpHeaders();
    }

    @Given("The endpoint for {string} is available for method {string}")
    public void theEndpointForIsAvailableForMethod(String endpoint, String method) {
        LoginDTO loginDTO = new LoginDTO("KarenWinter", "secret123");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity("/login", loginDTO, LoginResponseDTO.class);
        String token = loginResponse.getBody().token();

        httpHeaders.setBearerAuth(token);

        response = restTemplate
                .exchange("/" + endpoint,
                        HttpMethod.OPTIONS,
                        new HttpEntity<>(null, httpHeaders), // null because GET doesn't have a body
                        String.class);
        List<String> options = Arrays.stream(response.getHeaders()
                .get("Allow")
                .get(0)
                .split(","))
        .toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));
    }

    @When("I retrieve All BankAccounts")
    public void iRetrieveAllBankAccounts() {
        ParameterizedTypeReference<List<BankAccountResponseDTO>> responseType = new ParameterizedTypeReference<List<BankAccountResponseDTO>>() {};
        bankAccountsResponse = restTemplate
                .exchange("/BankAccounts",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        responseType);
        System.out.println(bankAccountsResponse);
    }


    @Then("I should receive all BankAccounts")
    public void iShouldReceiveAllBankAccounts() {
        Assertions.assertNotNull(bankAccountsResponse.getBody());
    }

    @When("I create a BankAccounts with type {string} and userId {int}")
    public void iCreateABankAccountsWithTypeAndUserId(String arg0, int arg1) {
    }

    @Then("The response status is {int}")
    public void theResponseStatusIs(int arg0) {
    }

    @And("The userId is {int}")
    public void theUserIdIs(int arg0) {
    }

    @When("I update a BankAccounts with status {string}")
    public void iUpdateABankAccountsWithStatus(String arg0) {
    }

    @And("The status is {string}")
    public void theStatusIs(String arg0) {
    }
}
