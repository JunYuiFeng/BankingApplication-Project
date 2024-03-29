package nl.inholland.bankingapplication.services.mappers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class BankAccountUpdateDTOMapper implements BiFunction<BankAccountUpdateDTO, BankAccount, BankAccount> {
    @Override
    public BankAccount apply(BankAccountUpdateDTO bankAccountUpdateDTO, BankAccount bankAccount) {
        if (bankAccountUpdateDTO.getStatusIgnoreCase() != null) {
            bankAccount.setStatus(bankAccountUpdateDTO.getStatusIgnoreCase());
        }

        if (bankAccountUpdateDTO.getAbsoluteLimit() != null) {
            bankAccount.setAbsoluteLimit(bankAccountUpdateDTO.getAbsoluteLimit());
        }

        return bankAccount;
    }
}

