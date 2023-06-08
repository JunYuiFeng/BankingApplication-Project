package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountResponseDTO;

import java.util.function.Function;

public class UserAccountResponseDTOMapper implements Function<UserAccount, UserAccountResponseDTO> {
    @Override
    public UserAccountResponseDTO apply(UserAccount userAccount) {
        return new UserAccountResponseDTO(
                userAccount.getId(),
                userAccount.getFirstName(),
                userAccount.getLastName(),
                userAccount.getEmail(),
                userAccount.getUsername(),
                userAccount.getType(),
                userAccount.getStatus(),
                userAccount.getPhoneNumber(),
                userAccount.getBsn(),
                userAccount.getDayLimit(),
                userAccount.getCurrentDayLimit(),
                userAccount.getTransactionLimit());
    }
}
