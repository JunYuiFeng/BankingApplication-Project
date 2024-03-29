package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.services.BankAccountService;
import nl.inholland.bankingapplication.services.UserAccountService;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import com.fasterxml.jackson.databind.ObjectMapper;



// Just in case, we use @ExtendWith to ensure the context is loaded.
// We use @WebMvcTest because it allows us to only test the controller without starting the full spring boot application and loading in all the dependencies (repositories, services etc.)
@ExtendWith(SpringExtension.class)
@WebMvcTest(BankAccountController.class)
//@Import(JWTFilter.class)

public class BankAccountControllerTest {

    // We use mockMvc to simulate HTTP requests to a controller class
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService bankAccountService;
    @MockBean
    private UserAccountService userAccountService;
    @MockBean
    private JWTTokeProvider jwtTokeProvider; // Mock or test implementation


    private BankAccount bankAccount1;
    private BankAccount bankAccount2;

    private UserAccount userAccount;
    private BankAccountResponseDTO bankAccountResponseDTO1;
    private BankAccountResponseDTO bankAccountResponseDTO2;

    @BeforeEach
    void init() {
        userAccount = new UserAccount();
        userAccount.setId(2L);
        userAccount.setUsername("JunFeng");
        userAccount.setFirstName("Jun");
        bankAccount1 = new BankAccount("NL77ABNA5602795901", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, userAccount);
        bankAccount2 = new BankAccount("NL77ABNA5602795902", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, userAccount);
        bankAccountResponseDTO1 = new BankAccountResponseDTO("NL77ABNA5602795901", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, userAccount);
        bankAccountResponseDTO2 = new BankAccountResponseDTO("NL77ABNA5602795902", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, userAccount);
    }

    @Test
    @WithMockUser(username = "JunFeng", roles = {"EMPLOYEE"})
    void getAllBankAccountsShouldReturnAListOfBankAccounts() throws Exception {
        // Prepare the expected bank accounts
        List<BankAccountResponseDTO> expectedBankAccounts = List.of(bankAccountResponseDTO1, bankAccountResponseDTO2);
        when(bankAccountService.getAllBankAccounts()).thenReturn(expectedBankAccounts);

        // Perform the GET request and assert the response
        this.mockMvc.perform(MockMvcRequestBuilders.get("/BankAccounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].iban", is(bankAccountResponseDTO1.getIBAN())))
                .andExpect(jsonPath("$[0].type", is(bankAccountResponseDTO1.getType().toString())))
                .andExpect(jsonPath("$[0].status", is(bankAccountResponseDTO1.getStatus().toString())))
                .andExpect(jsonPath("$[0].balance", is(bankAccountResponseDTO1.getBalance())))
                .andExpect(jsonPath("$[0].absoluteLimit", is(bankAccountResponseDTO1.getAbsoluteLimit())))
                .andExpect(jsonPath("$[0].userAccount.username", is(bankAccountResponseDTO1.getUserAccount().getUsername())))
                .andExpect(jsonPath("$[1].iban", is(bankAccountResponseDTO2.getIBAN())))
                .andExpect(jsonPath("$[1].type", is(bankAccountResponseDTO2.getType().toString())))
                .andExpect(jsonPath("$[1].status", is(bankAccountResponseDTO2.getStatus().toString())))
                .andExpect(jsonPath("$[1].balance", is(bankAccountResponseDTO2.getBalance())))
                .andExpect(jsonPath("$[1].absoluteLimit", is(bankAccountResponseDTO2.getAbsoluteLimit())))
                .andExpect(jsonPath("$[1].userAccount.username", is(bankAccountResponseDTO2.getUserAccount().getUsername())));
    }

    @Test
    @WithMockUser(username = "JunFeng", roles = {"EMPLOYEE"})
    void addBankAccountReturnsCreatedStatus() throws Exception {
        // Prepare the request body
        BankAccountRegisterDTO requestBody = new BankAccountRegisterDTO();
        requestBody.setType(BankAccountType.CURRENT);
        requestBody.setUserId(2L);

        // Serialize the request body to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        when(bankAccountService.addBankAccount(any(BankAccountRegisterDTO.class))).thenReturn(bankAccountResponseDTO1);

        mockMvc.perform(MockMvcRequestBuilders.post("/BankAccounts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBodyJson))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = "JunFeng", roles = {"EMPLOYEE"})
    @Test
    void getBankAccountByIBANShouldReturnBankAccount() throws Exception {
        String iban = "NL77ABNA5602795901";

        // Mock the bankAccountService to return a BankAccountResponseDTO object
        when(bankAccountService.getBankAccountByIBAN(iban)).thenReturn(bankAccount1);

        // Perform the GET request to the endpoint with the IBAN value
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/BankAccounts/{IBAN}", iban))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban", is(bankAccount1.getIBAN())))
                .andExpect(jsonPath("$.type", is(bankAccount1.getType().toString())))
                .andExpect(jsonPath("$.status", is(bankAccount1.getStatus().toString())))
                .andExpect(jsonPath("$.balance", is(bankAccount1.getBalance())))
                .andExpect(jsonPath("$.absoluteLimit", is(bankAccount1.getAbsoluteLimit())))
                .andExpect(jsonPath("$.userAccount.username", is(bankAccount1.getUserAccount().getUsername())));
    }

    @WithMockUser(username = "JunFeng", roles = {"EMPLOYEE"})
    @Test
    void getBankAccountByUserAccountIdShouldReturnBankAccounts() throws Exception {
        Long id = 2L;

        // Mock the bankAccountService to return a list of BankAccountResponseDTO objects
        List<BankAccount> expectedBankAccounts = List.of(bankAccount1, bankAccount2);
        when(bankAccountService.getBankAccountsByUserAccountId(id)).thenReturn(expectedBankAccounts);

        // Perform the GET request to the endpoint with the user account ID
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/BankAccounts/UserAccount/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].iban", is(bankAccount1.getIBAN())))
                .andExpect(jsonPath("$[0].type", is(bankAccount1.getType().toString())))
                .andExpect(jsonPath("$[0].status", is(bankAccount1.getStatus().toString())))
                .andExpect(jsonPath("$[0].balance", is(bankAccount1.getBalance())))
                .andExpect(jsonPath("$[0].absoluteLimit", is(bankAccount1.getAbsoluteLimit())))
                .andExpect(jsonPath("$[0].userAccount.username", is(bankAccount1.getUserAccount().getUsername())))
                .andExpect(jsonPath("$[1].iban", is(bankAccount2.getIBAN())))
                .andExpect(jsonPath("$[1].type", is(bankAccount2.getType().toString())))
                .andExpect(jsonPath("$[1].status", is(bankAccount2.getStatus().toString())))
                .andExpect(jsonPath("$[1].balance", is(bankAccount2.getBalance())))
                .andExpect(jsonPath("$[1].absoluteLimit", is(bankAccount2.getAbsoluteLimit())))
                .andExpect(jsonPath("$[1].userAccount.username", is(bankAccount2.getUserAccount().getUsername())));
    }

    @WithMockUser(username = "JunFeng", roles = {"EMPLOYEE"})
    @Test
    void getBankAccountsByStatusShouldReturnBankAccounts() throws Exception {
        String status = "ACTIVE";

        List<BankAccountResponseDTO> expectedBankAccounts = List.of(bankAccountResponseDTO1, bankAccountResponseDTO2);
        when(bankAccountService.getBankAccountsByStatus(status)).thenReturn(expectedBankAccounts);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/BankAccounts")
                        .param("status", status))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].iban", is(bankAccountResponseDTO1.getIBAN())))
                .andExpect(jsonPath("$[0].type", is(bankAccountResponseDTO1.getType().toString())))
                .andExpect(jsonPath("$[0].status", is(bankAccountResponseDTO1.getStatus().toString())))
                .andExpect(jsonPath("$[0].balance", is(bankAccountResponseDTO1.getBalance())))
                .andExpect(jsonPath("$[0].absoluteLimit", is(bankAccountResponseDTO1.getAbsoluteLimit())))
                .andExpect(jsonPath("$[0].userAccount.username", is(bankAccountResponseDTO1.getUserAccount().getUsername())))
                .andExpect(jsonPath("$[1].iban", is(bankAccountResponseDTO2.getIBAN())))
                .andExpect(jsonPath("$[1].type", is(bankAccountResponseDTO2.getType().toString())))
                .andExpect(jsonPath("$[1].status", is(bankAccountResponseDTO2.getStatus().toString())))
                .andExpect(jsonPath("$[1].balance", is(bankAccountResponseDTO2.getBalance())))
                .andExpect(jsonPath("$[1].absoluteLimit", is(bankAccountResponseDTO2.getAbsoluteLimit())))
                .andExpect(jsonPath("$[1].userAccount.username", is(bankAccountResponseDTO2.getUserAccount().getUsername())));
    }

    @WithMockUser(username = "JunFeng", roles = {"EMPLOYEE"})
    @Test
    void getBankAccountByNameShouldReturnBankAccounts() throws Exception {
        String name = "Jun";

        List<BankAccountResponseDTO> expectedBankAccounts = List.of(bankAccountResponseDTO1, bankAccountResponseDTO2);
        when(bankAccountService.getBankAccountsByName(name)).thenReturn(expectedBankAccounts);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/BankAccounts")
                        .param("name", name))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].iban", is(bankAccountResponseDTO1.getIBAN())))
                .andExpect(jsonPath("$[0].type", is(bankAccountResponseDTO1.getType().toString())))
                .andExpect(jsonPath("$[0].status", is(bankAccountResponseDTO1.getStatus().toString())))
                .andExpect(jsonPath("$[0].balance", is(bankAccountResponseDTO1.getBalance())))
                .andExpect(jsonPath("$[0].absoluteLimit", is(bankAccountResponseDTO1.getAbsoluteLimit())))
                .andExpect(jsonPath("$[0].userAccount.username", is(bankAccountResponseDTO1.getUserAccount().getUsername())))
                .andExpect(jsonPath("$[1].iban", is(bankAccountResponseDTO2.getIBAN())))
                .andExpect(jsonPath("$[1].type", is(bankAccountResponseDTO2.getType().toString())))
                .andExpect(jsonPath("$[1].status", is(bankAccountResponseDTO2.getStatus().toString())))
                .andExpect(jsonPath("$[1].balance", is(bankAccountResponseDTO2.getBalance())))
                .andExpect(jsonPath("$[1].absoluteLimit", is(bankAccountResponseDTO2.getAbsoluteLimit())))
                .andExpect(jsonPath("$[1].userAccount.username", is(bankAccountResponseDTO2.getUserAccount().getUsername())));
    }

    @WithMockUser(username = "JunFeng", roles = {"EMPLOYEE"})
    @Test
    void updateBankAccountShouldReturnCreatedStatus() throws Exception {
        BankAccountUpdateDTO requestBody = new BankAccountUpdateDTO();
        requestBody.setStatus("ACTIVE");

        String iban = "NL77ABNA5602795901";

        // Serialize the request body to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        when(bankAccountService.updateBankAccount(any(BankAccountUpdateDTO.class), eq(iban))).thenReturn(bankAccountResponseDTO1);

        mockMvc.perform(MockMvcRequestBuilders.patch("/BankAccounts/{IBAN}", iban).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBodyJson))
                .andExpect(status().isCreated());
    }
}
