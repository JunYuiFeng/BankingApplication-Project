package nl.inholland.bankingapplication.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserType;

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
    private List<UserType> type;

    public UserAccount(String firstName, String lastName, String email, String username, String password, List<UserType> type) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
            this.type = type;
    }

}
