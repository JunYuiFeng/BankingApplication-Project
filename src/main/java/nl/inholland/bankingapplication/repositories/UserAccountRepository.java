package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

     Optional<UserAccount> findUserAccountByUsername(String username);

     @Query("SELECT ua FROM UserAccount ua WHERE ua.type = :type")
     List<UserAccount> findUserAccountsWithType(UserAccountType type);
}


