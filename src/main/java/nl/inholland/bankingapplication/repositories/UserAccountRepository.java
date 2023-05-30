package nl.inholland.bankingapplication.repositories;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

     Optional<UserAccount> findUserAccountByUsername(String username);

     @Query("SELECT u FROM UserAccount u WHERE u.types = 'ROLE_USER'")
     List<UserAccount> findUserAccountsByTypeee();
}


