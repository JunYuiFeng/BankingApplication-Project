package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountDTO {
    private String IBAN;
    //private int userId;
    private String type;
    private String status;
    private double balance;
}
