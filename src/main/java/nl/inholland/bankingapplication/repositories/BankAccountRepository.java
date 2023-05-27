package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long>{
    List<BankAccount> findBankAccountByUserAccountFirstNameContainingIgnoreCaseOrUserAccountLastNameContainingIgnoreCase(String firstName, String lastName);
    List<BankAccount> findBankAccountByUserAccountId(Long id); //optional doesn't work with List
    Optional<BankAccount> findBankAccountByIBAN(String IBAN);
    List<BankAccount> findBankAccountByStatus(BankAccountStatus status);
}
