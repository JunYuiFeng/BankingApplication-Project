package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

     Optional<UserAccount> findUserAccountByUsername(String username);
}


