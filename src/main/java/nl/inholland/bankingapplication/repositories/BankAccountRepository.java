package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.enums.BankAccountStatus;
import nl.inholland.bankingapplication.models.enums.BankAccountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long>{
    List<BankAccount> findBankAccountByUserAccountFirstNameIgnoreCaseOrUserAccountLastNameIgnoreCase(String firstName, String lastName);
    List<BankAccount> findBankAccountByUserAccountId(Long id); //optional doesn't work with List
    Optional<BankAccount> findBankAccountByIBAN(String IBAN);
    List<BankAccount> findBankAccountByStatus(BankAccountStatus status);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.userAccount <> ?1")
    List<BankAccount> findAllExceptOwnAccount(UserAccount userAccount);
    boolean existsByUserAccountIdAndType(Long id, BankAccountType type);
}
