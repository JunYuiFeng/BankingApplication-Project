package nl.inholland.bankingapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue
    private Long id;
    private String IBAN;
    private String type;
    private String status;
    private double balance;
    //private double limit;

    @OneToOne
    private UserAccount userAccount;

    public BankAccount(String IBAN, String type, String status, double balance, UserAccount userAccount) {
        this.IBAN = IBAN;
        this.userAccount = userAccount;
        this.status = status;
        this.type = type;
        this.balance = balance;
    }
}
