package nl.inholland.bankingapplication.models;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.UserAccount;

import java.sql.Timestamp;

@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    public Long id;
    public double amount;
    @OneToOne
    public UserAccount madeBy;
    @OneToOne
    public BankAccount accountFrom;
    @OneToOne
    public BankAccount accountTo;
    public String description;
    public Timestamp occuredAt;

    public Transaction(double amount, UserAccount madeBy, BankAccount accountFrom, BankAccount accountTo, String description, Timestamp occuredAt) {
        this.amount = amount;
        this.madeBy = madeBy;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.description = description;
        this.occuredAt = occuredAt;
    }
}
