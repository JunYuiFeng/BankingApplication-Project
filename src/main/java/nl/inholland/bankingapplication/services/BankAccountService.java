package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.repositories.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {
    private BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<BankAccount> getAllBankAccounts() {
        return (List<BankAccount>) bankAccountRepository.findAll();
    }

    public BankAccount getBankAccountById(Long id) {
        return bankAccountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("BankAccount not found")
        );
    }

    public BankAccount addBankAccount(BankAccountDTO dto) {
        return bankAccountRepository.save(this.mapDtoToBankAccount(dto));
    }

    public BankAccount deactivateBankAccount(Long id) {
        BankAccount bankAccountToUpdate = bankAccountRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bank Account not found"));
        bankAccountToUpdate.setStatus("inactive");

        return bankAccountRepository.save(bankAccountToUpdate);
    }

    private BankAccount mapDtoToBankAccount(BankAccountDTO dto) {
        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setType(dto.getType());
        newBankAccount.setStatus(dto.getStatus());
        newBankAccount.setBalance(dto.getBalance());
        newBankAccount.setIBAN(dto.getIBAN());

        return newBankAccount;
    }
}
