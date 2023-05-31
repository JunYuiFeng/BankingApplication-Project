package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import org.hibernate.loader.access.BaseNaturalIdLoadAccessImpl;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDTO {
    public Long id;
    public double amount;
    public UserAccount madeBy;
    public BankAccount accountFrom;
    public BankAccount accountTo;
    public String description;
    public Timestamp occuredAt;

    public TransactionDTO(double amount, UserAccount madeBy, BankAccount accountFrom, BankAccount accountTo, String description, Timestamp occuredAt) {
        this.amount = amount;
        this.madeBy = madeBy;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.description = description;
        this.occuredAt = occuredAt;
    }
}
