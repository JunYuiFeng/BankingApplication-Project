package nl.inholland.bankingapplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountPatchDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountResponseDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountUpdateDTO;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.services.UserAccountService;
import nl.inholland.bankingapplication.services.mappers.UserAccountDTOMapper;
import nl.inholland.bankingapplication.services.mappers.UserAccountResponseDTOMapper;
import nl.inholland.bankingapplication.services.mappers.UserAccountUpdateDTOMapper;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UserAccountController.class)
public class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private JWTTokeProvider jwtTokeProvider;

    private UserAccountResponseDTO userAccountResponseDTO1;

    private UserAccountResponseDTO userAccountResponseDTO2;

    private UserAccountResponseDTO userAccountResponseDTO3;

    private UserAccountDTO userAccountDTO;

    private UserAccountDTOMapper userAccountDTOMapper;

    private ObjectMapper objectMapper;


    @BeforeEach
    void init(){
        userAccountResponseDTO1 = new UserAccountResponseDTO(1L, "Jason", "Xie", "Jasonxie@gmail.com", "JasonXie", UserAccountType.ROLE_USER, UserAccountStatus.ACTIVE, "+31681111111", 784935753, 2500, 0, 250);
        userAccountResponseDTO2 = new UserAccountResponseDTO(2L, "Viktor", "Cheese", "ViktorCheese@gmail.com", "ViktorCheese", UserAccountType.ROLE_USER, UserAccountStatus.ACTIVE, "+31682222222", 784935753, 1000, 0, 250);
        userAccountResponseDTO3 = new UserAccountResponseDTO(3L, "John", "Doe", "JohnDoe@gmail.com", "JohnDoe", UserAccountType.ROLE_CUSTOMER, UserAccountStatus.ACTIVE, "+31333333333", 12345333, 1000, 0, 250);
        userAccountDTO = new UserAccountDTO("Viktor", "Cheese", "ViktorCheese@gmail.com", "ViktorCheese", "secret123", "+31682222222", 784935753);
        userAccountDTOMapper = new UserAccountDTOMapper();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser
    void getAllUserAccountsShouldReturnAListOfTwo() throws Exception{
        when(userAccountService.getAllUserAccounts()).thenReturn(List.of(userAccountResponseDTO1, userAccountResponseDTO2));

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/UserAccounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void getAllUserAccountsShouldReturnNotFoundIfUrlIsIncorrect() throws Exception{
        when(userAccountService.getAllUserAccounts()).thenReturn(List.of(userAccountResponseDTO1, userAccountResponseDTO2));

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/UserAccount"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllRegisteredUserAccountsShouldReturnListOfRegisteredUserAccounts() throws Exception {
        List<UserAccountResponseDTO> registeredUserAccounts = List.of(userAccountResponseDTO1, userAccountResponseDTO2);

        when(userAccountService.getAllRegisteredUserAccounts()).thenReturn(registeredUserAccounts);

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/UserAccounts/registered"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(registeredUserAccounts.size())));
    }

    @Test
    @WithMockUser
    void getUserAccountByIdShouldReturnUserAccountForSpecifiedId() throws Exception {
        Long userId = 1L;

        UserAccount user = userAccountDTOMapper.apply(userAccountDTO);
        when(userAccountService.getUserAccountById(userId)).thenReturn(user);

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/UserAccounts/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value(userAccountResponseDTO2.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userAccountResponseDTO2.getLastName()))
                .andExpect(jsonPath("$.email").value(userAccountResponseDTO2.getEmail()))
                .andExpect(jsonPath("$.username").value(userAccountResponseDTO2.getUsername()))
                .andExpect(jsonPath("$.type").value(userAccountResponseDTO2.getType().toString()))
                .andExpect(jsonPath("$.status").value(userAccountResponseDTO2.getStatus().toString()))
                .andExpect(jsonPath("$.phoneNumber").value(userAccountResponseDTO2.getPhoneNumber()))
                .andExpect(jsonPath("$.bsn").value(userAccountResponseDTO2.getBsn()))
                .andExpect(jsonPath("$.dayLimit").value(userAccountResponseDTO2.getDayLimit()))
                .andExpect(jsonPath("$.currentDayLimit").value(userAccountResponseDTO2.getCurrentDayLimit()))
                .andExpect(jsonPath("$.transactionLimit").value(userAccountResponseDTO2.getTransactionLimit()));
    }

    @Test
    @WithMockUser
    void getUserAccountByIdShouldReturnNotFoundIfIdDoesNotExist() throws Exception {
        Long userId = 999L;

        when(userAccountService.getUserAccountById(userId)).thenThrow(new EntityNotFoundException());

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/UserAccounts/{id}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void getUserAccountByUsernameShouldReturnUserAccountForSpecifiedUsername() throws Exception {
        String username = "JasonXie";

        UserAccount user = userAccountDTOMapper.apply(userAccountDTO);
        when(userAccountService.getUserAccountByUsername(username)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/UserAccounts/username/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(userAccountResponseDTO2.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userAccountResponseDTO2.getLastName()))
                .andExpect(jsonPath("$.email", is(userAccountResponseDTO2.getEmail())))
                .andExpect(jsonPath("$.username", is(userAccountResponseDTO2.getUsername())))
                .andExpect(jsonPath("$.type", is(userAccountResponseDTO2.getType().toString())))
                .andExpect(jsonPath("$.status", is(userAccountResponseDTO2.getStatus().toString())))
                .andExpect(jsonPath("$.phoneNumber", is(userAccountResponseDTO2.getPhoneNumber())))
                .andExpect(jsonPath("$.bsn", is(userAccountResponseDTO2.getBsn())))
                .andExpect(jsonPath("$.dayLimit", is(userAccountResponseDTO2.getDayLimit())))
                .andExpect(jsonPath("$.currentDayLimit", is(userAccountResponseDTO2.getCurrentDayLimit())))
                .andExpect(jsonPath("$.transactionLimit", is(userAccountResponseDTO2.getTransactionLimit())));

        verify(userAccountService).getUserAccountByUsername(username);
    }

    @Test
    @WithMockUser
    void getUserAccountByUsernameShouldReturnNotFoundIfUsernameDoesNotExist() throws Exception {
        String username = "JasonXie";

        when(userAccountService.getUserAccountByUsername(username)).thenThrow(new EntityNotFoundException());

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/UserAccounts/username/{username}", username))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllUserAccountsExceptOneShouldReturnListOfUserAccountsExceptSpecifiedId() throws Exception {
        Long userId = 1L;
        when(userAccountService.getAllUserAccountsExceptOne(userId)).thenReturn(List.of(userAccountResponseDTO2, userAccountResponseDTO3));

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/UserAccounts/Exclude/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userAccountResponseDTO2.getId()))
                .andExpect(jsonPath("$[0].firstName").value(userAccountResponseDTO2.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(userAccountResponseDTO2.getLastName()))
                .andExpect(jsonPath("$[0].email").value(userAccountResponseDTO2.getEmail()))
                .andExpect(jsonPath("$[0].username").value(userAccountResponseDTO2.getUsername()))
                .andExpect(jsonPath("$[0].type").value(userAccountResponseDTO2.getType().toString()))
                .andExpect(jsonPath("$[0].status").value(userAccountResponseDTO2.getStatus().toString()))
                .andExpect(jsonPath("$[0].phoneNumber").value(userAccountResponseDTO2.getPhoneNumber()))
                .andExpect(jsonPath("$[0].bsn").value(userAccountResponseDTO2.getBsn()))
                .andExpect(jsonPath("$[0].dayLimit").value(userAccountResponseDTO2.getDayLimit()))
                .andExpect(jsonPath("$[0].currentDayLimit").value(userAccountResponseDTO2.getCurrentDayLimit()))
                .andExpect(jsonPath("$[0].transactionLimit").value(userAccountResponseDTO2.getTransactionLimit()));

        verify(userAccountService).getAllUserAccountsExceptOne(userId);
    }

    @Test
    @WithMockUser
    void getAllUserAccountsExceptOneShouldReturnNotFoundIfIdDoesNotExist() throws Exception {
        Long userId = 999L;

        when(userAccountService.getAllUserAccountsExceptOne(userId)).thenThrow(new EntityNotFoundException());

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/UserAccounts/Exclude/{id}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void addUserAccountShouldReturnCreatedStatus() throws Exception {
        when(userAccountService.addUserAccount(any(UserAccountDTO.class))).thenReturn(userAccountResponseDTO2);

        mockMvc.perform(MockMvcRequestBuilders.post("/UserAccounts/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(userAccountDTO)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(userAccountResponseDTO2.getId()));
    }

    @Test
    @WithMockUser
    public void deleteUserAccountShouldDeleteUserAccountWithSpecifiedID() throws Exception {
        Long id = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/UserAccounts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userAccountService).deleteUserAccount(id);
    }

    @Test
    @WithMockUser
    public void updateUserAccountShouldUpdateUserAccountWithSpecifiedIDAndReturnUpdatedUserAccount() throws Exception {
        Long id = 2L;

        when(userAccountService.updateUserAccount(eq(id), any(UserAccountUpdateDTO.class))).thenReturn(userAccountResponseDTO2);

        mockMvc.perform(MockMvcRequestBuilders.put("/UserAccounts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAccountDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userAccountResponseDTO2.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value(userAccountResponseDTO2.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userAccountResponseDTO2.getLastName()))
                .andExpect(jsonPath("$.email").value(userAccountResponseDTO2.getEmail()))
                .andExpect(jsonPath("$.username").value(userAccountResponseDTO2.getUsername()))
                .andExpect(jsonPath("$.type").value(userAccountResponseDTO2.getType().toString()))
                .andExpect(jsonPath("$.status").value(userAccountResponseDTO2.getStatus().toString()))
                .andExpect(jsonPath("$.phoneNumber").value(userAccountResponseDTO2.getPhoneNumber()))
                .andExpect(jsonPath("$.bsn").value(userAccountResponseDTO2.getBsn()))
                .andExpect(jsonPath("$.dayLimit").value(userAccountResponseDTO2.getDayLimit()))
                .andExpect(jsonPath("$.currentDayLimit").value(userAccountResponseDTO2.getCurrentDayLimit()))
                .andExpect(jsonPath("$.transactionLimit").value(userAccountResponseDTO2.getTransactionLimit()));

        verify(userAccountService).updateUserAccount(eq(id), any(UserAccountUpdateDTO.class));
    }

    @Test
    @WithMockUser
    public void patchUserAccountShouldPartiallyUpdateUserAccountWithSpecifiedStatusAndOrCurrentDayLimitAndReturnUpdatedUserAccount() throws Exception {
        long id = 1L;
        UserAccountPatchDTO userAccountPatchDTO = new UserAccountPatchDTO();
        userAccountPatchDTO.setStatus(UserAccountStatus.ACTIVE);
        userAccountPatchDTO.setCurrentDayLimit(0);

        when(userAccountService.patchUserAccount(eq(id), any(UserAccountPatchDTO.class))).thenReturn(userAccountResponseDTO1);

        mockMvc.perform(MockMvcRequestBuilders.patch("/UserAccounts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAccountPatchDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userAccountResponseDTO1.getId().intValue()))
                .andExpect(jsonPath("$.firstName").value(userAccountResponseDTO1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userAccountResponseDTO1.getLastName()))
                .andExpect(jsonPath("$.email").value(userAccountResponseDTO1.getEmail()))
                .andExpect(jsonPath("$.username").value(userAccountResponseDTO1.getUsername()))
                .andExpect(jsonPath("$.type").value(userAccountResponseDTO1.getType().toString()))
                .andExpect(jsonPath("$.status").value(userAccountResponseDTO1.getStatus().toString()))
                .andExpect(jsonPath("$.phoneNumber").value(userAccountResponseDTO1.getPhoneNumber()))
                .andExpect(jsonPath("$.bsn").value(userAccountResponseDTO1.getBsn()))
                .andExpect(jsonPath("$.dayLimit").value(userAccountResponseDTO1.getDayLimit()))
                .andExpect(jsonPath("$.currentDayLimit").value(userAccountResponseDTO1.getCurrentDayLimit()))
                .andExpect(jsonPath("$.transactionLimit").value(userAccountResponseDTO1.getTransactionLimit()));

        verify(userAccountService).patchUserAccount(eq(id), any(UserAccountPatchDTO.class));
    }
}
