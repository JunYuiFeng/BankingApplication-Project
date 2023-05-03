package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.BankAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long>{
}
