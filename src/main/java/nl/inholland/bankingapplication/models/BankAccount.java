package nl.inholland.bankingapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;


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

    @OneToOne
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
