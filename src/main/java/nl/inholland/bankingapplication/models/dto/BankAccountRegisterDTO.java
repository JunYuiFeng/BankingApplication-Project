package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.BankAccountType;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountRegisterDTO {
    private BankAccountType type;
    private Long userId;
}
