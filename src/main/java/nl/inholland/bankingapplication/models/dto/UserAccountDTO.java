package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccountDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private List<String> types;
    private String phoneNumber;
    private int bsn;
    private double dayLimit;
    private double transactionLimit;

    public List<UserAccountType> getTypeIgnoreCase() {
        List<UserAccountType> list = new ArrayList<>();
        for (String t:types) {
            list.add(UserAccountType.valueOf(t.toUpperCase()));
        }
        return list;
    }
}
