package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccountPatchDTO {
    private UserAccountStatus status;
    private double currentDayLimit;

    public UserAccountStatus getStatusIgnoreCase(String status) {
        return UserAccountStatus.valueOf(status.toUpperCase());
    }
}