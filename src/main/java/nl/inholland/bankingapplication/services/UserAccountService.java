package nl.inholland.bankingapplication.services;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.UserAccountStatus;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import nl.inholland.bankingapplication.services.mappers.UserAccountResponseDTOMapper;
import nl.inholland.bankingapplication.util.JWTTokeProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.*;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private  final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTTokeProvider jwtTokeProvider;

    private final UserAccountResponseDTOMapper userAccountResponseDTOMapper;

    public UserAccountService(UserAccountRepository userAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JWTTokeProvider jwtTokeProvider) {
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokeProvider = jwtTokeProvider;
        this.userAccountResponseDTOMapper = new UserAccountResponseDTOMapper();
    }

    public List<UserAccountResponseDTO> getAllUserAccounts() {
        try{
            return getUserAccountResponseDTOS((List<UserAccount>) userAccountRepository.findAll());
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No user accounts found");
        }
    }



    public List<UserAccountResponseDTO> getAllUserAccountsExceptOne(Long id) {
        try{
            return getUserAccountResponseDTOS(userAccountRepository.getAllUserAccountsExceptOne(id));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No user accounts found");
        }
    }

    public List<UserAccountResponseDTO> getAllRegisteredUserAccounts() {
        try{
            return getUserAccountResponseDTOS(userAccountRepository.findUserAccountsWithType(UserAccountType.ROLE_USER));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No user accounts found");
        }

    }

    public UserAccount getUserAccountById(Long id) {
        return userAccountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("UserAccount not found")
        );
    }

    public UserAccount getUserAccountByUsername(String username) {
        return userAccountRepository.findUserAccountByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Username " + username + " not found")
        );
    }

    public UserAccountResponseDTO addUserAccount(UserAccountDTO userAccount) {
        try {
            validateFields(userAccount);
            if (userAccountRepository.findUserAccountByUsername(userAccount.getUsername()).isEmpty()) {
                userAccount.setPassword(bCryptPasswordEncoder.encode(userAccount.getPassword()));
                UserAccount user = userAccountRepository.save(this.mapDtoToUserAccount(userAccount));
                return userAccountResponseDTOMapper.apply(user);
            } else {
                throw new IllegalArgumentException("User already exists");
            }
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateFields(UserAccountDTO user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Please provide a username");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Please provide a password");
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Please provide a first name");
        }

        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Please provide a last name");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Please provide an email");
        } else {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!user.getEmail().matches(emailRegex)) {
                throw new IllegalArgumentException("Invalid email");
            }
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Please provide a phone number");
        } else {
            String phoneNumberRegex = "^\\+\\d{2}\\d{9}$";
            if (!user.getPhoneNumber().matches(phoneNumberRegex)) {
                throw new IllegalArgumentException("Invalid phone number");
            }
        }

        if (Objects.isNull(user.getBsn())) {
            throw new IllegalArgumentException("Please provide a BSN");
        } else {
            String bsnString = String.valueOf(user.getBsn());
            String bsnRegex = "^\\d{9}$";
            if (!bsnString.matches(bsnRegex)) {
                throw new IllegalArgumentException("Invalid BSN");
            }
        }
    }

    public UserAccountResponseDTO addPredefinedUserAccount(UserAccountPredefinedDTO userAccount) {
        try{
            if(userAccountRepository.findUserAccountByUsername(userAccount.getUsername()).isEmpty()){
                userAccount.setPassword(bCryptPasswordEncoder.encode(userAccount.getPassword()));
                UserAccount user = userAccountRepository.save(this.mapPredefinedDtoToUserAccount(userAccount));
                return userAccountResponseDTOMapper.apply(user);
            }
            else {
                throw new IllegalArgumentException("User already exists");
            }
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }



    public void deleteUserAccount(Long id) {
        try {
            if (userAccountRepository.findById(id).get().getBankAccounts().size() > 0 || userAccountRepository.findById(id).get().getType() != UserAccountType.ROLE_USER) {
                throw new IllegalArgumentException("User with accounts cannot be deleted.");
            }
            else {
                userAccountRepository.deleteById(id);
            }
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("UserAccount not found");
        }
    }

    public UserAccountResponseDTO updateUserAccount(Long id, UserAccountUpdateDTO userAccountDTO) {
        try {
            UserAccount userAccountToUpdate = userAccountRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("UserAccount not found"));

            mapDtoToUserAccountUpdate(userAccountDTO, userAccountToUpdate);
            UserAccount user = userAccountRepository.save(userAccountToUpdate);
            return userAccountResponseDTOMapper.apply(user);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Unable to update user account");
        }
    }

    public UserAccountResponseDTO patchUserAccount(Long id, UserAccountPatchDTO userAccountPatchDTO) {
        try{
            UserAccount userAccountToUpdate = userAccountRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("UserAccount not found"));

            if (Objects.nonNull(userAccountPatchDTO.getStatus())) {
                userAccountToUpdate.setStatus(userAccountPatchDTO.getStatus());
            }

            if (Objects.nonNull(userAccountPatchDTO.getCurrentDayLimit())) {
                userAccountToUpdate.setDayLimit(userAccountPatchDTO.getCurrentDayLimit());
            }

            UserAccount user = userAccountRepository.save(userAccountToUpdate);
            return userAccountResponseDTOMapper.apply(user);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Unable to patch user account");
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Execute at midnight every day
    public void updateCurrentDayLimit() {
        try {
            List<UserAccount> userAccounts = new ArrayList<>();
            userAccountRepository.findAll().forEach(userAccounts::add);

            userAccounts.stream()
                    .peek(userAccount -> userAccount.setCurrentDayLimit(0)) //peek change the daylimit of each userAccount without transforming the stream
                    .forEach(userAccountRepository::save);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Unable to update current day limit");
        }
    }

    private List<UserAccountResponseDTO> getUserAccountResponseDTOS(List<UserAccount> users) {
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No user accounts found");
        }

        return users.stream()
                .map(userAccountResponseDTOMapper)
                .toList();
    }

    private UserAccount mapDtoToUserAccount(UserAccountDTO dto) {
        UserAccount newUserAccount = new UserAccount();
        newUserAccount.setFirstName(dto.getFirstName());
        newUserAccount.setLastName(dto.getLastName());
        newUserAccount.setEmail(dto.getEmail());
        newUserAccount.setUsername(dto.getUsername());
        newUserAccount.setPassword(dto.getPassword());
        newUserAccount.setType(UserAccountType.ROLE_USER);
        newUserAccount.setStatus(UserAccountStatus.ACTIVE);
        newUserAccount.setPhoneNumber(dto.getPhoneNumber());
        newUserAccount.setBsn((dto.getBsn()));
        newUserAccount.setDayLimit(2500);
        newUserAccount.setCurrentDayLimit(0);
        newUserAccount.setTransactionLimit(1000);
        return newUserAccount;
    }

    private UserAccount mapPredefinedDtoToUserAccount(UserAccountPredefinedDTO dto){
        UserAccount newUserAccount = new UserAccount();
        newUserAccount.setFirstName(dto.getFirstName());
        newUserAccount.setLastName(dto.getLastName());
        newUserAccount.setEmail(dto.getEmail());
        newUserAccount.setUsername(dto.getUsername());
        newUserAccount.setPassword(dto.getPassword());
        newUserAccount.setType(dto.getType());
        newUserAccount.setStatus(dto.getStatus());
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
        if (user.getStatus().equals(UserAccountStatus.INACTIVE)) {
            throw new AuthenticationException("User has been deactivated");
        }

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
