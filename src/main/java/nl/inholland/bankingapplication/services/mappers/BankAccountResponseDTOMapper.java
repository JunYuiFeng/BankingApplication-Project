package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountResponseDTO;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BankAccountResponseDTOMapper implements Function<BankAccount, BankAccountResponseDTO> {
    @Override
    public BankAccountResponseDTO apply(BankAccount bankAccount) {
        return new BankAccountResponseDTO(
                bankAccount.getIBAN(),
                bankAccount.getType(),
                bankAccount.getStatus(),
                bankAccount.getBalance(),
                bankAccount.getAbsoluteLimit(),
                bankAccount.getUserAccount());
    }
}
