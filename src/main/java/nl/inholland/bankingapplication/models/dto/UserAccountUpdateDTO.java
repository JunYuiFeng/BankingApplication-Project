package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccountUpdateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private UserAccountType type;
    private String phoneNumber;
    private int bsn;
    private double dayLimit;
    private double transactionLimit;

    public UserAccountType getTypeIgnoreCase() {
        return UserAccountType.valueOf(type.toString().toUpperCase());
    }
}