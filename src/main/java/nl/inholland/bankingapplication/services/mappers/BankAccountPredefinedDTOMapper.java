package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountPredefinedDTO;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BankAccountPredefinedDTOMapper implements Function<BankAccountPredefinedDTO, BankAccount> {
    private final UserAccountService userAccountService;

    public BankAccountPredefinedDTOMapper(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public BankAccount apply(BankAccountPredefinedDTO bankAccountPredefinedDTO) {
        return new BankAccount(
                bankAccountPredefinedDTO.getIBAN(),
                bankAccountPredefinedDTO.getType(),
                bankAccountPredefinedDTO.getStatus(),
                bankAccountPredefinedDTO.getBalance(),
                bankAccountPredefinedDTO.getAbsoluteLimit(),
                userAccountService.getUserAccountById(bankAccountPredefinedDTO.getUserId())
        );
    }
}
