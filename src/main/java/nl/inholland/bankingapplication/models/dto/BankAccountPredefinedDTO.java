package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountPredefinedDTO {
    private String IBAN;
    private BankAccountType type;
    private BankAccountStatus status;
    private double balance;
    private double absoluteLimit;
    private Long userId;
}
