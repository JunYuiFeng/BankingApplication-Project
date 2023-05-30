package nl.inholland.bankingapplication.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
public class BankAccount {

    @Id
    private String IBAN;
    private BankAccountType type;
    private BankAccountStatus status;
    private double balance;
    private double absoluteLimit;

    @ManyToOne
    @JsonIgnoreProperties("bankAccounts") //ignored during serialization and deserialization processes performed by Jackson, prevent infinite recursion
    private UserAccount userAccount;

    public BankAccount(String IBAN, BankAccountType type, BankAccountStatus status, double balance, double absoluteLimit, UserAccount userAccount) {
        this.IBAN = IBAN;
        this.userAccount = userAccount;
        this.status = status;
        this.absoluteLimit = absoluteLimit;
        this.type = type;
        this.balance = balance;
    }
}
