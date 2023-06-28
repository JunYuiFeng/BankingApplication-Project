package nl.inholland.bankingapplication.controllers;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import jakarta.annotation.Nullable;
import nl.inholland.bankingapplication.configuration.WebSecurityConf;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.Transaction;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.services.BankAccountService;
import nl.inholland.bankingapplication.services.TransactionService;
import nl.inholland.bankingapplication.services.UserAccountService;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Just in case, we use @ExtendWith to ensure the context is loaded.
// We use @WebMvcTest because it allows us to only test the controller without starting the full spring boot application and loading in all the dependencies (repositories, services etc.)
@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
//@Import(JWTFilter.class)

public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private TransactionService transactionService;
    @MockBean BankAccountService bankAccountService;
    @MockBean
    private UserAccountService userAccountService;
    @MockBean
    private Transaction transaction;
    @MockBean
    private JWTTokeProvider jwtTokeProvider; // Mock or test implementation


    private BankAccount bankAccount;
    private TransactionResponseDTO transactionResponseDTO;
    @BeforeEach
    void init() {
        bankAccount = new BankAccount("NL77ABNA5602795901", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, userAccountService.getUserAccountById(2L));
        userAccountService.addPredefinedUserAccount(new UserAccountPredefinedDTO("Jun", "Feng", "junfeng@gmail.com", "JunFeng", "secret123", UserAccountType.ROLE_CUSTOMER, UserAccountStatus.ACTIVE, "+31222222222", 12345222, 1000.00,0, 250.00));
        bankAccountService.addPredefinedBankAccount( new BankAccountPredefinedDTO("NL71RABO3667086008", BankAccountType.CURRENT, BankAccountStatus.ACTIVE, 1000.00, 0, 2L));
        transactionResponseDTO = new TransactionResponseDTO(1L, 1000.00, 0, "NL71RABO3667086008", "NL77ABNA5602795901", null, null);
    }
    
    @Test
    @WithMockUser
    void MakeTransactionReturnsCreatedStatus() throws Exception {
        when(transactionService.makeTransaction(any(MakeTransactionDTO.class),any(UserAccount.class))).thenReturn(transactionResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/Transactions").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"accountFrom\": \"NL71RABO3667086008\", \"accountTo\": \"NL77ABNA5602795901\", \"amount\": \"10.00\", \"description\": \"null\"}"))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser
    void MakeDepositReturnsCreatedStatus() throws Exception{
        when(transactionService.makeDeposit(any(WithdrawalAndDepositRequestDTO.class),any(UserAccount.class))).thenReturn(transactionResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/Transactions/Deposit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"IBAN\": \"NL71RABO3667086008\", \"amount\": \"10.00\"}"))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser
    void MakeWithdrawalReturnsCreatedStatus() throws Exception{
        when(transactionService.makeDeposit(any(WithdrawalAndDepositRequestDTO.class),any(UserAccount.class))).thenReturn(transactionResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/Transactions/Withdrawal").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"IBAN\": \"NL71RABO3667086008\", \"amount\": \"10.00\"}"))
                .andExpect(status().isCreated());
    }
    
}
