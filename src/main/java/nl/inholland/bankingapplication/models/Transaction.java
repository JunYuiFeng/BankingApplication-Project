package nl.inholland.bankingapplication.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    public Long id;
    public Double amount;
    @ManyToOne
    public UserAccount madeBy;
    @ManyToOne
    public BankAccount accountFrom;
    @ManyToOne
    public BankAccount accountTo;
    @Nullable
    public String description;
    public Timestamp occuredAt;

    public Transaction(Double amount, UserAccount madeBy, BankAccount accountFrom, BankAccount accountTo, @Nullable String description, Timestamp occuredAt) {
        this.amount = amount;
        this.madeBy = madeBy;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.description = description;
        this.occuredAt = occuredAt;
    }
}
