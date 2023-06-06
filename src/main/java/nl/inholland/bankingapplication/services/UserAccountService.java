package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountUpdateDTO;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private  final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTTokeProvider jwtTokeProvider;
    public UserAccountService(UserAccountRepository userAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JWTTokeProvider jwtTokeProvider) {
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokeProvider = jwtTokeProvider;
    }

    public List<UserAccount> getAllUserAccounts() {
        return (List<UserAccount>) userAccountRepository.findAll();
    }

    public List<UserAccount> getAllRegisteredUserAccounts() {
            return userAccountRepository.findUserAccountsWithType(UserAccountType.ROLE_USER);
    }

    public UserAccount getUserAccountById(Long id) {
        return userAccountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("UserAccount not found")
        );
    }

    public UserAccount getUserAccountByUsername(String username) {
        return userAccountRepository.findUserAccountByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Username " + username + " not found")
        );
    }

    public UserAccount addUserAccount(UserAccountDTO userAccount) {
        //return //userAccountRepository.save(this.mapDtoToUserAccount(userAccount));
         if(userAccountRepository.findUserAccountByUsername(userAccount.getUsername()).isEmpty()){
          userAccount.setPassword(bCryptPasswordEncoder.encode(userAccount.getPassword()));
          return userAccountRepository.save(this.mapDtoToUserAccount(userAccount));
        }
         throw new IllegalArgumentException("User already exists");
    }

    public void deleteUserAccount(Long id) {
        userAccountRepository.deleteById(id);
    }

    public UserAccount updateUserAccount(Long id, UserAccountUpdateDTO userAccountDTO) {
        UserAccount userAccountToUpdate = userAccountRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("UserAccount not found"));

        mapDtoToUserAccountUpdate(userAccountDTO, userAccountToUpdate);


        return userAccountRepository.save(userAccountToUpdate);
    }

    @Scheduled(cron = "0 0 0 * * *") // Execute at midnight every day
    public void updateCurrentDayLimit() {
        List<UserAccount> userAccounts = new ArrayList<>();
        userAccountRepository.findAll().forEach(userAccounts::add);

        userAccounts.stream()
                .peek(userAccount -> userAccount.setCurrentDayLimit(0)) //peek change the daylimit of each userAccount without transforming the stream
                .forEach(userAccountRepository::save);
    }

    private UserAccount mapDtoToUserAccount(UserAccountDTO dto) {
        UserAccount newUserAccount = new UserAccount();
        newUserAccount.setFirstName(dto.getFirstName());
        newUserAccount.setLastName(dto.getLastName());
        newUserAccount.setEmail(dto.getEmail());
        newUserAccount.setUsername(dto.getUsername());
        newUserAccount.setPassword(dto.getPassword());
        newUserAccount.setType(dto.getTypeIgnoreCase());
        newUserAccount.setPhoneNumber(dto.getPhoneNumber());
        newUserAccount.setBsn((dto.getBsn()));
        newUserAccount.setDayLimit(dto.getDayLimit());
        newUserAccount.setCurrentDayLimit(dto.getCurrentDayLimit());
        newUserAccount.setTransactionLimit(dto.getTransactionLimit());
        return newUserAccount;
    }

    private UserAccount mapDtoToUserAccountUpdate(UserAccountUpdateDTO userAccountDTO, UserAccount userAccountToUpdate) {
        userAccountToUpdate.setFirstName(userAccountDTO.getFirstName());
        userAccountToUpdate.setLastName(userAccountDTO.getLastName());
        userAccountToUpdate.setEmail(userAccountDTO.getEmail());
        userAccountToUpdate.setUsername(userAccountDTO.getUsername());
        userAccountToUpdate.setType(userAccountDTO.getTypeIgnoreCase());
        userAccountToUpdate.setPhoneNumber(userAccountDTO.getPhoneNumber());
        userAccountToUpdate.setBsn(userAccountDTO.getBsn());
        userAccountToUpdate.setDayLimit(userAccountDTO.getDayLimit());
        userAccountToUpdate.setCurrentDayLimit(userAccountDTO.getCurrentDayLimit());
        userAccountToUpdate.setTransactionLimit(userAccountDTO.getTransactionLimit());

        return userAccountToUpdate;
    }

   /* public UserAccount login(String username, String password) {
        Optional<UserAccount> userAccount = userAccountRepository.findUserAccountByUsername(username);
        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null; // or throw an exception indicating invalid username or password
    }*/

    public String login(String username, String password) throws Exception {
// See if a user with the provided username exists or throw exception
        UserAccount user = this.userAccountRepository
                .findUserAccountByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));

// Check if the password hash matches

        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
// Return a JWT to the client
            return jwtTokeProvider.createToken(user.getId(), user.getUsername(), Collections.singletonList(user.getType()));
        } else {
            throw new AuthenticationException("Invalid username/password");
        }
    }

//    public UserAccount login(String username, String password){
//        //TODO change user account to USER please
//        UserAccount user = userAccountRepository.findUserAccountByUsername(username);
//        //once it is hashed, this will be changed
//        if(password.equals(user.getPassword())){
//            return user;
//        }else {
//            return null;
//        }
//    }

}
