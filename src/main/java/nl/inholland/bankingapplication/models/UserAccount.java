package nl.inholland.bankingapplication.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UserAccountType> types;

    private String phoneNumber;

    private int bsn;

    private double dayLimit;

    private double currentDayLimit;

    private double transactionLimit;

    private double currentTransactionLimit;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.EAGER)
    private List<BankAccount> bankAccounts = new ArrayList<>();



    public UserAccount(String firstName, String lastName, String email, String username, String password, List<UserAccountType> types, String phoneNumber, int bsn, double dayLimit, double transactionLimit, List<BankAccount> bankAccounts) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
            this.types = types;
            this.phoneNumber = phoneNumber;
            this.bsn = bsn;
            this.dayLimit = dayLimit;
            this.transactionLimit = transactionLimit;
            this.bankAccounts = bankAccounts;
    }
}
