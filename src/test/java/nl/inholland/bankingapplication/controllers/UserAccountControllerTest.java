package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.filter.JWTFilter;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.services.BankAccountService;
import nl.inholland.bankingapplication.services.UserAccountService;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BankAccountController.class)
@Import(JWTFilter.class)
public class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private JWTTokeProvider jwtTokeProvider;

    private UserAccount user1;

    private UserAccount user2;


    @BeforeEach
    void init(){
        user1 = new UserAccount("Jason", "Xie", "Jasonxie@gmail.com", "JasonXie", "secret123", UserAccountType.ROLE_CUSTOMER, "+31681111111", 784935753, 2500, 500, bankAccountService.getBankAccountsByUserAccountId(1L));
        user2 = new UserAccount("John", "Doe", "JohnDoe@gmail.com", "JohnDoe", "secret123", UserAccountType.ROLE_EMPLOYEE, "+31682222222", 849021437, 1500, 250, bankAccountService.getBankAccountsByUserAccountId(2L));
    }

//    @Test
//    @WithMockUser(username = "JohnDoe", roles = {"EMPLOYEE"})
//    void getAllUserAccountsShouldReturnAListOfTwo() throws Exception{
//        when(userAccountService.getAllUserAccounts()).thenReturn(List.of(user1, user2));
//
//        this.mockMvc.perform(
//                MockMvcRequestBuilders.get("/UserAccounts"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//    }

}
