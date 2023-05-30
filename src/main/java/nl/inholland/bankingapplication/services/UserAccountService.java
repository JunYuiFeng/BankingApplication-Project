package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
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

    public UserAccount updateUserAccount(Long id, UserAccountDTO userAccountDTO) {
        UserAccount userAccountToUpdate = userAccountRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("UserAccount not found"));

        setUserAccountFields(userAccountDTO, userAccountToUpdate);


        return userAccountRepository.save(userAccountToUpdate);
    }

    private UserAccount mapDtoToUserAccount(UserAccountDTO dto) {
        UserAccount newUserAccount = new UserAccount();
        setUserAccountFields(dto, newUserAccount);

        return newUserAccount;
    }

    private UserAccount setUserAccountFields(UserAccountDTO userAccountDTO, UserAccount userAccountToUpdate) {
        userAccountToUpdate.setFirstName(userAccountDTO.getFirstName());
        userAccountToUpdate.setLastName(userAccountDTO.getLastName());
        userAccountToUpdate.setEmail(userAccountDTO.getEmail());
        userAccountToUpdate.setUsername(userAccountDTO.getUsername());
        userAccountToUpdate.setPassword(userAccountDTO.getPassword());
        userAccountToUpdate.setTypes(List.of(userAccountDTO.getTypeIgnoreCase()));
        userAccountToUpdate.setPhoneNumber(userAccountDTO.getPhoneNumber());
        userAccountToUpdate.setBsn(userAccountDTO.getBsn());
        userAccountToUpdate.setDayLimit(userAccountDTO.getDayLimit());
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
            return jwtTokeProvider.createToken(user.getUsername(), user.getTypes());
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
