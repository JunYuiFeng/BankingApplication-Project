package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountUpdateDTO;

import java.util.function.BiFunction;

public class UserAccountUpdateDTOMapper implements BiFunction<UserAccountUpdateDTO,UserAccount, UserAccount> {
    @Override
    public UserAccount apply(UserAccountUpdateDTO userAccountDTO, UserAccount userAccount) {
        userAccount.setFirstName(userAccountDTO.getFirstName());
        userAccount.setLastName(userAccountDTO.getLastName());
        userAccount.setEmail(userAccountDTO.getEmail());
        userAccount.setUsername(userAccountDTO.getUsername());
        userAccount.setType(userAccountDTO.getType());
        userAccount.setPhoneNumber(userAccountDTO.getPhoneNumber());
        userAccount.setBsn(userAccountDTO.getBsn());
        userAccount.setDayLimit(userAccountDTO.getDayLimit());
        userAccount.setTransactionLimit(userAccountDTO.getTransactionLimit());
        return userAccount;
    }
}
