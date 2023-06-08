package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccountUpdateTypeDTO {
    private UserAccountType type;
}