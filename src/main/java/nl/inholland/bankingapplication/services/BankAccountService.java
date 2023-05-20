package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccounResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountStatusUpdateDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.repositories.BankAccountRepository;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserAccountService userAccountService;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserAccountService userAccountService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userAccountService = userAccountService;
    }

    public List<BankAccount> getAllBankAccounts() {
        return (List<BankAccount>) bankAccountRepository.findAll();
    }

    public BankAccount getBankAccountByIBAN(String IBAN) {
        return bankAccountRepository.findBankAccountByIBAN(IBAN).orElseThrow(
                () -> new EntityNotFoundException("Bank account not found")
        );
    }

    public BankAccount getBankAccountByFirstname(String firstname) {
        return bankAccountRepository.findBankAccountByUserAccountFirstName(firstname).orElseThrow(
                () -> new EntityNotFoundException("Bank account with firstname " + firstname + " not found")
        );
    }

    public BankAccount getBankAccountByUserAccountId(Long id) {
        return bankAccountRepository.findBankAccountByUserAccountId(id).orElseThrow(
                () -> new EntityNotFoundException("Bank account with id " + id + " not found")
        );
    }

    public BankAccounResponseDTO addBankAccount(BankAccountRegisterDTO dto) {
        BankAccount bankAccount = bankAccountRepository.save(this.mapDtoToBankAccount(dto));
        return mapBankAccountToDto(bankAccount);
    }
    
    public BankAccount updateBankAccountStatus(BankAccountStatusUpdateDTO dto, String IBAN) {
        BankAccount bankAccount = bankAccountRepository
                .findBankAccountByIBAN(IBAN)
                .orElseThrow(() -> new EntityNotFoundException("Bank Account not found"));
        bankAccount.setStatus(dto.getStatusIgnoreCase());

        return bankAccountRepository.save(bankAccount);
    }

    private BankAccount mapDtoToBankAccount(BankAccountRegisterDTO dto) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setType(dto.getType());
        bankAccount.setStatus(BankAccountStatus.ACTIVE);
        bankAccount.setBalance(0);
        bankAccount.setAbsoluteLimit(0);
        bankAccount.setIBAN(this.GenerateIBAN());
        bankAccount.setUserAccount(userAccountService.getUserAccountById(dto.getUserId()));

        return bankAccount;
    }

    private BankAccounResponseDTO mapBankAccountToDto(BankAccount bankAccount) {
        BankAccounResponseDTO bankAccounResponseDTO = new BankAccounResponseDTO();
        bankAccounResponseDTO.setType(bankAccount.getType());
        bankAccounResponseDTO.setStatus(bankAccount.getStatus());
        bankAccounResponseDTO.setBalance(bankAccount.getBalance());
        bankAccounResponseDTO.setAbsoluteLimit(bankAccount.getAbsoluteLimit());
        bankAccounResponseDTO.setIBAN(bankAccount.getIBAN());
        bankAccounResponseDTO.setUserId(bankAccount.getUserAccount().getId());

        return bankAccounResponseDTO;
    }

    public String GenerateIBAN(){
        Iban iban = Iban.random(CountryCode.NL);
        iban = Iban.random();
        iban = new Iban.Builder()
                .countryCode(CountryCode.NL)
                .bankCode("INHO")
                .buildRandom();
        return iban.toString();
    }
}
