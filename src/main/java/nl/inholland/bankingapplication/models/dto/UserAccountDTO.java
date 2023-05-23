package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

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
    private List<UserAccountType> types;
    private String phoneNumber;
    private int bsn;
    private double dayLimit;
    private double transactionLimit;

    public UserAccountType getTypeIgnoreCase() {
        return UserAccountType.valueOf(types.get(0).toString().toUpperCase());
    }
}
