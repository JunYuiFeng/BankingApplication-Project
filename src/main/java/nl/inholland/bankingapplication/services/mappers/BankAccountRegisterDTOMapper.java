package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BankAccountRegisterDTOMapper implements Function<BankAccountRegisterDTO, BankAccount> {
    private final UserAccountService userAccountService;

    public BankAccountRegisterDTOMapper(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public BankAccount apply(BankAccountRegisterDTO bankAccountRegisterDTO) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setType(bankAccountRegisterDTO.getType());
        bankAccount.setUserAccount(userAccountService.getUserAccountById(bankAccountRegisterDTO.getUserId()));
        return bankAccount;
    }
}
