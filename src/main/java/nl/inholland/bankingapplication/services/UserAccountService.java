package nl.inholland.bankingapplication.services;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.BankAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

        userAccountToUpdate.setFirstName(userAccountDTO.getFirstName());
        userAccountToUpdate.setLastName(userAccountDTO.getLastName());
        userAccountToUpdate.setEmail(userAccountDTO.getEmail());
        userAccountToUpdate.setUsername(userAccountDTO.getUsername());
        userAccountToUpdate.setPassword(userAccountDTO.getPassword());

        return userAccountRepository.save(userAccountToUpdate);

    }

    private UserAccount mapDtoToUserAccount(UserAccountDTO dto) {
        UserAccount newUserAccount = new UserAccount();
        newUserAccount.setFirstName(dto.getFirstName());
        newUserAccount.setLastName(dto.getLastName());
        newUserAccount.setEmail(dto.getEmail());
        newUserAccount.setUsername(dto.getUsername());
        newUserAccount.setPassword(dto.getPassword());
        newUserAccount.setType(dto.getType());

        return newUserAccount;
    }
    public UserAccount login(String username, String password){
        //TODO change user account to USER please
        UserAccount user = userAccountRepository.findUserAccountByUsername(username);
        //once it is hashed, this will be changed
        if(password.equals(user.getPassword())){
            return user;
        }else {
            return null;
        }
    }
}
