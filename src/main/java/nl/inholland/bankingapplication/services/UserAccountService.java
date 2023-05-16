package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
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
        return userAccountRepository.save(this.mapDtoToUserAccount(userAccount));
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
        userAccountToUpdate.setType(userAccountDTO.getType());
        userAccountToUpdate.setPhoneNumber(userAccountDTO.getPhoneNumber());
        userAccountToUpdate.setBsn(userAccountDTO.getBsn());
        userAccountToUpdate.setDayLimit(userAccountDTO.getDayLimit());
        userAccountToUpdate.setTransactionLimit(userAccountDTO.getTransactionLimit());

        return userAccountToUpdate;
    }

    public UserAccount login(String username, String password) {
        Optional<UserAccount> userAccount = userAccountRepository.findUserAccountByUsername(username);
        if (userAccount.isPresent()) {
            UserAccount user = userAccount.get();
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null; // or throw an exception indicating invalid username or password
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
