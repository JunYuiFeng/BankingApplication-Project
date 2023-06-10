package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WithdrawalAndDepositRequestDTO {
    String IBAN;
    Double amount;
}
