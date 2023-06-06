package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccounResponseDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class BankAccountResponseDTOMapper implements Function<BankAccount, BankAccounResponseDTO> {
    @Override
    public BankAccounResponseDTO apply(BankAccount bankAccount) {
        return new BankAccounResponseDTO(
                bankAccount.getIBAN(),
                bankAccount.getType(),
                bankAccount.getStatus(),
                bankAccount.getBalance(),
                bankAccount.getAbsoluteLimit(),
                bankAccount.getUserAccount());
    }
}
