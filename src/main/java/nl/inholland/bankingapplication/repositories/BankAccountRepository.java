package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.BankAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long>{
    Optional<BankAccount> findBankAccountByUserAccountFirstName(String firstname);
    Optional<BankAccount> findBankAccountByUserAccountId(Long id);
    Optional<BankAccount> findBankAccountByIBAN(String IBAN);
}
