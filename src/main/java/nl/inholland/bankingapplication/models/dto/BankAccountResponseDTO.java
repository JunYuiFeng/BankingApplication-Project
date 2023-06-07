package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountResponseDTO {
    private String IBAN;
    private BankAccountType type;
    private BankAccountStatus status;
    private double balance;
    private double absoluteLimit;
    private UserAccount userAccount;
}
