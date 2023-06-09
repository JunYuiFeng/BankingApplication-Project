package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountPredefinedDTO;

import java.util.function.Function;

public class UserAccountPredefinedDTOMapper implements Function<UserAccountPredefinedDTO, UserAccount> {
@Override
    public UserAccount apply(UserAccountPredefinedDTO userAccountDTO) {
        return new UserAccount(
                userAccountDTO.getFirstName(),
                userAccountDTO.getLastName(),
                userAccountDTO.getEmail(),
                userAccountDTO.getUsername(),
                userAccountDTO.getPassword(),
                userAccountDTO.getType(),
                userAccountDTO.getStatus(),
                userAccountDTO.getPhoneNumber(),
                userAccountDTO.getBsn(),
                userAccountDTO.getDayLimit(),
                userAccountDTO.getCurrentDayLimit(),
                userAccountDTO.getTransactionLimit(),
                null
        );
    }
}
