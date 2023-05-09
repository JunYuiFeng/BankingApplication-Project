package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.UserAccount;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MakeTransactionDTO {
    public String accountFrom;
    public String accountTo;
    public double amount;
    public String description;
}
