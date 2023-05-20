package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.enums.UserType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccountDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private List<UserType> type;
}
