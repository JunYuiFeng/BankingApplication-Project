package nl.inholland.bankingapplication.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
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
    public void iCreateABankAccountsWithTypeAndUserId(String type, int userId) throws JsonProcessingException {
        BankAccountRegisterDTO bankAccountRegisterDTO = new BankAccountRegisterDTO();
        bankAccountRegisterDTO.setType(BankAccountType.valueOf(type));
        bankAccountRegisterDTO.setUserId((long) userId);

        httpHeaders.add("Content-Type", "application/json");
        response = restTemplate.exchange("/BankAccounts",
                HttpMethod.POST,
                new HttpEntity<>(
                        mapper.writeValueAsString(bankAccountRegisterDTO),
                        httpHeaders
                ), String.class);
    }

    @Then("The response status is {int}")
    public void theResponseStatusIs(int status) {
        Assertions.assertEquals(status, response.getStatusCode().value());
    }

    @And("The userId is {int}")
    public void theUserIdIs(int userId) throws JsonProcessingException {
        BankAccount bankAccount = mapper.readValue(response.getBody(), BankAccount.class);
        Assertions.assertEquals(userId, bankAccount.getUserAccount().getId());
    }

    @When("I update a BankAccount with IBAN {string} to status {string}")
    public void iUpdateABankAccountWithIBANToStatus(String IBAN, String status) throws JsonProcessingException{
        BankAccountUpdateDTO bankAccountUpdateDTO = new BankAccountUpdateDTO();
        bankAccountUpdateDTO.setStatus(status);

        httpHeaders.add("Content-Type", "application/json");
        response = restTemplate.exchange("/BankAccounts/{IBAN}" + IBAN,
                HttpMethod.PATCH,
                new HttpEntity<>(
                        mapper.writeValueAsString(bankAccountUpdateDTO),
                        httpHeaders
                ), String.class);
    }

    @And("The status is {string}")
    public void theStatusIs(String status) throws JsonProcessingException {
        BankAccount bankAccount = mapper.readValue(response.getBody(), BankAccount.class);
        Assertions.assertEquals(status, bankAccount.getStatus().toString());
    }
}
