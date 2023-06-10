package nl.inholland.bankingapplication.model;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserAccountTest {
    private UserAccount userAccount;

    @BeforeEach
    void init() {
        userAccount = new UserAccount();
    }

    @Test
    void NewUserAccountShouldNotBeNull() {
        assert(userAccount != null);
    }

    @Test
    void UserConstructorShouldCreateNewUserAccount() {
        UserAccount userAccount = new UserAccount("John", "Doe", "JohnDoe@gmail.com", "JohnDoe", "password", UserAccountType.ROLE_USER, UserAccountStatus.ACTIVE, "0612345678", 123456789, 1000, 0, 1000, null);
        assert(userAccount != null);
    }

    @Test
    public void userAccountGetterAndSettersShouldGetAndSetUserAccountsProperlyTest() {
        UserAccount userAccount = new UserAccount();

        userAccount.setId(1L);
        userAccount.setFirstName("John");
        userAccount.setLastName("Doe");
        userAccount.setEmail("JohnDoe@gmail.com");
        userAccount.setUsername("JohnDoe");
        userAccount.setPassword("secret123");
        userAccount.setType(UserAccountType.ROLE_USER);
        userAccount.setStatus(UserAccountStatus.ACTIVE);
        userAccount.setPhoneNumber("123456789");
        userAccount.setBsn(123456789);
        userAccount.setDayLimit(1000.0);
        userAccount.setCurrentDayLimit(500.0);
        userAccount.setTransactionLimit(5000.0);

        Assertions.assertEquals(1L, userAccount.getId());
        Assertions.assertEquals("John", userAccount.getFirstName());
        Assertions.assertEquals("Doe", userAccount.getLastName());
        Assertions.assertEquals("JohnDoe@gmail.com", userAccount.getEmail());
        Assertions.assertEquals("JohnDoe", userAccount.getUsername());
        Assertions.assertEquals("secret123", userAccount.getPassword());
        Assertions.assertEquals(UserAccountType.ROLE_USER, userAccount.getType());
        Assertions.assertEquals(UserAccountStatus.ACTIVE, userAccount.getStatus());
        Assertions.assertEquals("123456789", userAccount.getPhoneNumber());
        Assertions.assertEquals(123456789, userAccount.getBsn());
        Assertions.assertEquals(1000.0, userAccount.getDayLimit());
        Assertions.assertEquals(500.0, userAccount.getCurrentDayLimit());
        Assertions.assertEquals(5000.0, userAccount.getTransactionLimit());
    }
}
