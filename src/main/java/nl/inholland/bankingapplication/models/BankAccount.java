package nl.inholland.bankingapplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
    //private int userId;
    private String type;
    private String status;
    private double balance;

    public BankAccount(String IBAN, String type, String status, double balance) {
        this.IBAN = IBAN;
        //this.userId = userId;
        this.status = status;
        this.type = type;
        this.balance = balance;
    }
}
