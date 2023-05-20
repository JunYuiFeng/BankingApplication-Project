package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.jwt.JwtTokenProvider;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.enums.UserType;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
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


    public String login(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, new ArrayList<UserType>());
        } catch (AuthenticationException ae) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid credentials");
        }
    }


//    public UserAccount login(String username, String password) {
//        Optional<UserAccount> userAccount = userAccountRepository.findUserAccountByUsername(username);
//        if (userAccount.isPresent()) {
//            UserAccount user = userAccount.get();
//            if (password.equals(user.getPassword())) {
//                return user;
//            }
//        }
//        return null; // or throw an exception indicating invalid username or password
//    }

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
