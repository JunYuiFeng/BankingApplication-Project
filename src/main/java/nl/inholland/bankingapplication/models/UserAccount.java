package nl.inholland.bankingapplication.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    //TODO: maybe change to enum later -Jason
    private String type;

    public UserAccount(String firstName, String lastName, String email, String username, String password, String type) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
            this.type = type;
    }
}
