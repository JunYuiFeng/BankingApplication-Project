package nl.inholland.bankingapplication.cucumber;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.LoginDTO;
import nl.inholland.bankingapplication.models.dto.LoginResponseDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
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
public class UserAccountStepDefinitions extends BaseStepDefinitions{
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private ResponseEntity<UserAccountResponseDTO> response;

    private HttpHeaders headers = new HttpHeaders();

    @Given("When the endpoint {string} is available for method {string}")
    public void whenTheEndPointIsAvailableForMethod(String endpoint, String method) {
        LoginDTO loginDTO = new LoginDTO("KarenWinter", "secret123");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity("/login", loginDTO, LoginResponseDTO.class);
        String token = loginResponse.getBody().token();

        headers.setBearerAuth(token);

        ResponseEntity<String> response = restTemplate.exchange("/" + endpoint, HttpMethod.OPTIONS, new HttpEntity<>(null, headers), String.class);
        List<String> options = Arrays.stream(response.getHeaders()
                .get("Allow")
                .get(0)
                .split(","))
                .toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));
    }

    @When("I retrieve all UserAccounts")
    public void iRetrieveAllUserAccounts() {
        response = restTemplate.exchange(
                "/UserAccounts",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<UserAccountResponseDTO>() {}
        );
    }

    @Then("the response should have status code {int}")
    public void theResponseShouldHaveStatusCode(int statusCode) {
        Assertions.assertEquals(response.getStatusCode().value(), statusCode);
    }

    @And("I should have a list of {int} UserAccounts")
    public void iShouldGetAListOfUserAccounts(int numberOfUsers) {
        int actualNumberOfUsers = JsonPath.read(response.getBody(), "$.size()");
        Assertions.assertEquals(numberOfUsers, actualNumberOfUsers);
    }
}
