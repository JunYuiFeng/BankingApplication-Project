package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccountResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private UserAccountType type;
    private String phoneNumber;
    private int bsn;
    private double dayLimit;
    private double currentDayLimit;
    private double transactionLimit;
}