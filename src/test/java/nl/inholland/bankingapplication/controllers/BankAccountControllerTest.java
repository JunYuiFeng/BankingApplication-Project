package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Just in case, we use @ExtendWith to ensure the context is loaded.
// We use @WebMvcTest because it allows us to only test the controller without starting the full spring boot application and loading in all the dependencies (repositories, services etc.)
@ExtendWith(SpringExtension.class)
@WebMvcTest(BankAccountController.class)
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


    private BankAccount bankAccount;
    //private BankAccount bankAccountRegister;

    @BeforeEach
    void init() {
        bankAccount = new BankAccount("NL77ABNA5602795901", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, userAccountService.getUserAccountById(2L));
    }

    @Test
    @WithMockUser
    void getAllBankAccountsShouldReturnAListOfOne() throws Exception {
        when(bankAccountService.getAllBankAccounts())
                .thenReturn(List.of(bankAccount));

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/BankAccounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser
    void postBankAccountsShouldReturn201() throws Exception {
        when(bankAccountService.addBankAccount(any(BankAccountRegisterDTO.class))).thenReturn(bankAccount);
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/BankAccounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.IBAN").value("NL77ABNA5602795901"));
    }
}
