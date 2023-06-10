package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;

import java.util.List;
import java.util.function.Function;

public class UserAccountDTOMapper implements Function<UserAccountDTO, UserAccount> {
    @Override
    public UserAccount apply(UserAccountDTO userAccountDTO) {
        return new UserAccount(
                userAccountDTO.getFirstName(),
                userAccountDTO.getLastName(),
                userAccountDTO.getEmail(),
                userAccountDTO.getUsername(),
                userAccountDTO.getPassword(),
                UserAccountType.ROLE_USER,
                UserAccountStatus.ACTIVE,
                userAccountDTO.getPhoneNumber(),
                userAccountDTO.getBsn(),
                1000,
                0,
                250,
                null
        );
    }
}
