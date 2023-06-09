package nl.inholland.bankingapplication.cucumber;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

public class UserStepDefinitions extends BaseStepDefinitions{
    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @Given("When the endpoint {string} is called with a {string} request")
    public void whenTheEndpointIsCalledWithARequest(String endpoint, String request) {
        response = restTemplate.exchange("/" + endpoint, HttpMethod.OPTIONS, new HttpEntity<>(null, new HttpHeaders()), String.class);
                List<String> options = Arrays.stream(response.getHeaders()
                        .get("Allow")
                        .get(0)
                        .split(","))
                        .toList();
        Assertions.assertTrue(options.contains(request.toUpperCase()));
    }

    @When("Ï retrieve all users")
    public void iRetrieveAllUsers() {
        response = restTemplate.exchange("/UserAccounts", HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()), String.class);
    }

    @Then("I should get a list of {int} users")
    public void iShouldGetAListOfUsers(int numberOfUsers) {
        int actualNumberOfUsers = JsonPath.read(response.getBody(), "$.size()");
        Assertions.assertEquals(numberOfUsers, actualNumberOfUsers);
    }
}
