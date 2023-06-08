package nl.inholland.bankingapplication.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
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

    //@ElementCollection(fetch = FetchType.EAGER)
    private UserAccountType type;

    private UserAccountStatus status;

    private String phoneNumber;

    private int bsn;

    private double dayLimit;

    private double currentDayLimit;

    private double transactionLimit;


    @OneToMany(mappedBy = "userAccount", fetch = FetchType.EAGER)
    private List<BankAccount> bankAccounts = new ArrayList<>();



    public UserAccount(String firstName, String lastName, String email, String username, String password, UserAccountType type, UserAccountStatus status, String phoneNumber, int bsn, double dayLimit, double transactionLimit, List<BankAccount> bankAccounts) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
            this.type = type;
            this.status = status;
            this.phoneNumber = phoneNumber;
            this.bsn = bsn;
            this.dayLimit = dayLimit;
            this.transactionLimit = transactionLimit;
            this.bankAccounts = bankAccounts;
            this.currentDayLimit=0;
    }
}
