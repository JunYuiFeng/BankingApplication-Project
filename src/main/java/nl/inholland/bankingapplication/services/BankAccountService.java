package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountPredefinedDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import nl.inholland.bankingapplication.repositories.BankAccountRepository;
import nl.inholland.bankingapplication.services.mappers.BankAccountPredefinedDTOMapper;
import nl.inholland.bankingapplication.services.mappers.BankAccountRegisterDTOMapper;
import nl.inholland.bankingapplication.services.mappers.BankAccountResponseDTOMapper;
import nl.inholland.bankingapplication.services.mappers.BankAccountUpdateDTOMapper;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserAccountService userAccountService;
    private final BankAccountResponseDTOMapper bankAccountResponseDTOMapper;
    private final BankAccountUpdateDTOMapper bankAccountUpdateDTOMapper;
    private final BankAccountPredefinedDTOMapper bankAccountPredefinedDTOMapper;
    private final BankAccountRegisterDTOMapper bankAccountRegisterDTOMapper;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserAccountService userAccountService,
                              BankAccountResponseDTOMapper bankAccountResponseDTOMapper, BankAccountUpdateDTOMapper bankAccountUpdateDTOMapper,
                              BankAccountPredefinedDTOMapper bankAccountPredefinedDTOMapper, BankAccountRegisterDTOMapper bankAccountRegisterDTOMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.userAccountService = userAccountService;
        this.bankAccountResponseDTOMapper = bankAccountResponseDTOMapper;
        this.bankAccountUpdateDTOMapper = bankAccountUpdateDTOMapper;
        this.bankAccountPredefinedDTOMapper = bankAccountPredefinedDTOMapper;
        this.bankAccountRegisterDTOMapper = bankAccountRegisterDTOMapper;
    }

    public List<BankAccountResponseDTO> getAllBankAccounts() {
        try {
            List<BankAccount> bankAccounts = (List<BankAccount>) bankAccountRepository.findAll();

            if (bankAccounts.isEmpty()) {
                throw new EntityNotFoundException("No bank accounts found.");
            }

            return bankAccounts.stream()
                    .map(bankAccountResponseDTOMapper)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong: " + e);
        }
    }


    public List<BankAccountResponseDTO> getBankAccountsExceptOwnAccount(Long userId) {
        try {
            List<BankAccount> bankAccounts = bankAccountRepository.findAllExceptOwnAccount(userAccountService.getUserAccountById(userId)); //already returns a list so no need to cast

            if (bankAccounts.isEmpty()) {
                throw new EntityNotFoundException("No bank accounts found.");
            }

            return bankAccounts.stream()
                    .map(bankAccountResponseDTOMapper)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong: " + e);
        }
    }

    public BankAccount getBankAccountByIBAN(String IBAN) {
        return bankAccountRepository.findBankAccountByIBAN(IBAN)
                .orElseThrow(() -> new EntityNotFoundException("Bank account with IBAN " + IBAN + " not found" ));
    }

    public List<BankAccount> getBankAccountsByUserAccountId(Long id) {
        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByUserAccountId(id);
        if (bankAccounts.isEmpty()) {
            throw new EntityNotFoundException("Bank accounts not found");
        }
        return bankAccounts;
    }

    public List<BankAccountResponseDTO> getBankAccountsByStatus(String status) {
        try {
            List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByStatus(BankAccountStatus.valueOf(status.toUpperCase()));
            if (bankAccounts.isEmpty()) {
                throw new EntityNotFoundException("No bank accounts found with status " + status);
            }
            return bankAccounts.stream()
                    .map(bankAccountResponseDTOMapper)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong: " + e);
        }
    }

    public List<BankAccountResponseDTO> getBankAccountsByName(String name) {
        try {
            List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByUserAccountFirstNameIgnoreCaseOrUserAccountLastNameIgnoreCase(name, name);
            if (bankAccounts.isEmpty()) {
                throw new EntityNotFoundException("No bank accounts found with name '" + name + "'");
            }
            return bankAccounts.stream()
                    .map(bankAccountResponseDTOMapper)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong: " + e);
        }
    }

    public Boolean hasSavingsAccount(Long userId) {
        return bankAccountRepository.existsByUserAccountIdAndType(userId, BankAccountType.SAVINGS);
    }
    public BankAccountResponseDTO addBankAccount(BankAccountRegisterDTO dto) {
        try {
            if (this.hasSavingsAccount(dto.getUserId()) && dto.getType().equals(BankAccountType.SAVINGS)) {
                throw new DataIntegrityViolationException("User already has a savings account, only 1 savings account can be created");
            }

            BankAccount bankAccount = bankAccountRegisterDTOMapper.apply(dto);
            bankAccount.setStatus(BankAccountStatus.ACTIVE);
            bankAccount.setBalance(0);
            bankAccount.setAbsoluteLimit(0);
            bankAccount.setIBAN(this.GenerateIBAN());

            return bankAccountResponseDTOMapper.apply(bankAccountRepository.save(bankAccount));
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Failed to add bank account: " + e);
        }
    }

    public BankAccountResponseDTO addPredefinedBankAccount(BankAccountPredefinedDTO dto) {
        try {
            BankAccount bankAccount = bankAccountRepository.save(bankAccountPredefinedDTOMapper.apply(dto));
            return bankAccountResponseDTOMapper.apply(bankAccount);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Failed to add bank account: " + e);
        }
    }


    public BankAccountResponseDTO updateBankAccount(BankAccountUpdateDTO dto, String IBAN) {
        try {
            BankAccount bankAccount = this.getBankAccountByIBAN(IBAN);

            return bankAccountResponseDTOMapper.apply(bankAccountRepository.save(bankAccountUpdateDTOMapper.apply(dto, bankAccount)));
        } catch(Exception e) {
            throw new DataIntegrityViolationException("Failed to update bank account: " + e);
        }
    }


    public BankAccount updateAmount(String IBAN, double change){
        BankAccount bankAccountToUpdate = this.getBankAccountByIBAN(IBAN);
        double currentBalance = bankAccountToUpdate.getBalance();
        double newBalance = currentBalance + change;
        bankAccountToUpdate.setBalance(newBalance);
        return  bankAccountRepository.save(bankAccountToUpdate);
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
