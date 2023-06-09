package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.dto.UserAccountResponseDTO;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.services.UserAccountService;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@WebMvcTest(UserAccountController.class)
public class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private JWTTokeProvider jwtTokeProvider;

    private UserAccountResponseDTO user1;

    private UserAccountResponseDTO user2;


    @BeforeEach
    void init(){
        user1 = new UserAccountResponseDTO(1L, "Jason", "Xie", "Jasonxie@gmail.com", "JasonXie", UserAccountType.ROLE_CUSTOMER, UserAccountStatus.ACTIVE, "+31681111111", 784935753, 2500, 0, 250);
        user2 = new UserAccountResponseDTO(3L, "John", "Doe", "JohnDoe@gmail.com", "JohnDoe", UserAccountType.ROLE_CUSTOMER, UserAccountStatus.ACTIVE, "+31333333333", 12345333, 1000, 0, 250);
    }

    @Test
    @WithMockUser(username = "KarenWinter", roles = {"EMPLOYEE"})
    void getAllUserAccountsShouldReturnAListOfTwo() throws Exception{
        when(userAccountService.getAllUserAccounts()).thenReturn(List.of(user1, user2));

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/UserAccounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
