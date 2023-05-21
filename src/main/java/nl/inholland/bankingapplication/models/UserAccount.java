package nl.inholland.bankingapplication.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

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

    private UserAccountType type;

    private String phoneNumber;

    private int bsn;

    private double dayLimit;

    private double transactionLimit;



    public UserAccount(String firstName, String lastName, String email, String username, String password, UserAccountType type, String phoneNumber, int bsn, double dayLimit, double transactionLimit) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
            this.type = type;
            this.phoneNumber = phoneNumber;
            this.bsn = bsn;
            this.dayLimit = dayLimit;
            this.transactionLimit = transactionLimit;
    }
}
