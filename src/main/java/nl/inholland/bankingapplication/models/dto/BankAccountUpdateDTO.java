package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountUpdateDTO {
    String status;
    Double absoluteLimit;

    public BankAccountStatus getStatusIgnoreCase() {
        if (status != null) {
            return BankAccountStatus.valueOf(status.toUpperCase());
        }
        return null;
    }
}
