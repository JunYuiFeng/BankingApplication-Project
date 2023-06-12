package nl.inholland.bankingapplication.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserAccountsStepDefinitions extends BaseStepDefinitions {
    @Autowired
    private TestRestTemplate restTemplate;

    private final HttpHeaders headers;

    private ResponseEntity<String> response;

    private ResponseEntity <List<UserAccountResponseDTO>> userAccountsResponse;

    private ResponseEntity <UserAccountResponseDTO> userAccountResponse;

    private final ObjectMapper mapper;

    public UserAccountsStepDefinitions() {
       headers  = new HttpHeaders();
         mapper = new ObjectMapper();
    }
    @Given("When the endpoint {string} is available for method {string}")
    public void whenTheEndPointIsAvailableForMethod(String endpoint, String method) {
        LoginDTO loginDTO = new LoginDTO("KarenWinter", "secret123");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity("/login", loginDTO, LoginResponseDTO.class);
        String token = Objects.requireNonNull(loginResponse.getBody()).token();

        headers.setBearerAuth(token);

        response = restTemplate.exchange(
                "/" + endpoint,
                HttpMethod.OPTIONS,
                new HttpEntity<>(null, headers),
                String.class
        );

        List<String> options = Arrays.stream(Objects.requireNonNull(response.getHeaders()
                                .get("Allow"))
                .get(0)
                .split(","))
                .toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));
    }

    @When("I retrieve all UserAccounts")
    public void iRetrieveAllUserAccounts() {
        ParameterizedTypeReference<List<UserAccountResponseDTO>> responseType = new ParameterizedTypeReference<>() {
        };
        userAccountsResponse = restTemplate.exchange(
                "/UserAccounts",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                responseType
        );
    }

    @Then("the response should have status code {int}")
    public void theResponseShouldHaveStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, response.getStatusCode().value());
    }

    @And("I should have a list of {int} UserAccounts")
    public void iShouldGetAListOfUserAccounts(int numberOfUsers) {
        int actualNumberOfUsers = Objects.requireNonNull(userAccountsResponse.getBody()).size();
        Assertions.assertEquals(numberOfUsers, actualNumberOfUsers);
    }

    @When("I retrieve a UserAccount with id {int}")
    public void iRetrieveAUserAccountWithId(int id) {
        userAccountResponse = restTemplate.exchange(
                "/UserAccounts/" + id,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @And("I should have a UserAccount with id {int}")
    public void iShouldHaveAUserAccountWithId(int actualId) {
        Assertions.assertEquals(actualId, Objects.requireNonNull(userAccountResponse.getBody()).getId());
    }

    @When("I retrieve a UserAccount with username {string}")
    public void iRetrieveAUserAccountWithUsername(String username) {
        userAccountResponse = restTemplate.exchange(
                "/UserAccounts/username/" + username,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @And("I should have a UserAccount with username {string}")
    public void iShouldHaveAUserAccountWithUsername(String username) {
        Assertions.assertEquals(username, Objects.requireNonNull(userAccountResponse.getBody()).getUsername());
    }

    @When("I create a new UserAccount")
    public void iCreateNewUserAccount() throws JsonProcessingException {
        UserAccountDTO userAccountDTO = new UserAccountDTO();
        userAccountDTO.setFirstName("Bill");
        userAccountDTO.setLastName("Gates");
        userAccountDTO.setEmail("BillGates@gmail.com");
        userAccountDTO.setUsername("BillGates");
        userAccountDTO.setPassword("secret123");
        userAccountDTO.setPhoneNumber("+31612345678");
        userAccountDTO.setBsn(438795081);

        headers.add("Content-Type", "application/json");

        ParameterizedTypeReference<UserAccountResponseDTO> responseType = new ParameterizedTypeReference<>() {
        };
        userAccountResponse = restTemplate.exchange(
                "/UserAccounts/register",
                HttpMethod.POST,
                new HttpEntity<>(mapper.writeValueAsString(userAccountDTO), headers),
                responseType
        );
    }

    @When("I update a UserAccount with id {int}")
    public void iUpdateAUserAccountWithId(int id) throws JsonProcessingException {
        UserAccountUpdateDTO userAccountUpdateDTO = new UserAccountUpdateDTO();
        userAccountUpdateDTO.setFirstName("Jun");
        userAccountUpdateDTO.setLastName("Feng");
        userAccountUpdateDTO.setEmail("JunFeng@gmail.com");
        userAccountUpdateDTO.setUsername("JunFeng");
        userAccountUpdateDTO.setType(UserAccountType.ROLE_CUSTOMER);
        userAccountUpdateDTO.setPhoneNumber("+31685478372");
        userAccountUpdateDTO.setBsn(493207589);
        userAccountUpdateDTO.setDayLimit(2500);
        userAccountUpdateDTO.setTransactionLimit(250);

        headers.add("Content-Type", "application/json");

        ParameterizedTypeReference<UserAccountResponseDTO> responseType = new ParameterizedTypeReference<>() {
        };

        userAccountResponse = restTemplate.exchange(
                "/UserAccounts/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(mapper.writeValueAsString(userAccountUpdateDTO), headers),
                responseType
        );
    }

    @When("I delete a UserAccount with id {int}")
    public void iDeleteAUserAccountWithId(int id) {
        restTemplate.exchange(
                "/UserAccounts/" + id,
                HttpMethod.DELETE,
                new HttpEntity<>(null, headers),
                String.class
        );
    }

//    @When("I patch a UserAccount with id {int} with status {string}")
//    public void iPatchAUserAccountWithId2WithStatusINACTIVE(int id, String status) throws JsonProcessingException {
//        UserAccountPatchDTO userAccountPatchDTO = new UserAccountPatchDTO();
//        UserAccountStatus userAccountStatus = userAccountPatchDTO.getStatusIgnoreCase(status);
//        userAccountPatchDTO.setStatus(userAccountStatus);
//
//        headers.add("Content-Type", "application/json");
//        response = restTemplate.exchange(
//                "/UserAccounts/" + id,
//                HttpMethod.PATCH,
//                new HttpEntity<>(mapper.writeValueAsString(userAccountPatchDTO), headers),
//                String.class
//        );
//    }

//    @And("I should have a UserAccount with id {int} and status {string}")
//    public void iShouldHaveAUserAccountWithIdAndStatus(int id, String status) {
//        Assertions.assertEquals(id, Objects.requireNonNull(userAccountResponse.getBody()).getId());
//        Assertions.assertEquals(status, userAccountResponse.getBody().getStatus());
//    }
}
