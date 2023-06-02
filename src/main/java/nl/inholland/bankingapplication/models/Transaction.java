package nl.inholland.bankingapplication.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.UserAccount;

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

    public Transaction(Double amount, UserAccount madeBy, BankAccount accountFrom, BankAccount accountTo, String description, Timestamp occuredAt) {
        this.amount = amount;
        this.madeBy = madeBy;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.description = description;
        this.occuredAt = occuredAt;
    }
}
