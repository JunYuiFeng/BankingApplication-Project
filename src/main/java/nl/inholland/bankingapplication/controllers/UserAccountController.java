package nl.inholland.bankingapplication.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.*;
import nl.inholland.bankingapplication.models.enums.UserAccountType;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("UserAccounts")
public class UserAccountController {
    private UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<UserAccountResponseDTO>> getAllUserAccounts() {
        try{
            return ResponseEntity.ok(userAccountService.getAllUserAccounts());
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("Exclude/{id}")
    public ResponseEntity<List<UserAccountResponseDTO>> getAllUserAccountsExceptOne(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(userAccountService.getAllUserAccountsExceptOne(id));
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("registered")
    public ResponseEntity<List<UserAccountResponseDTO>> getAllRegisteredUserAccounts() {
        try{
            return ResponseEntity.ok(userAccountService.getAllRegisteredUserAccounts());
        } catch (EntityNotFoundException e) {
            //return this.handleException(404, e);
            return null;
        }
    }

    @PreAuthorize("principal.username == @userAccountService.getUserAccountById(#id).username || hasRole('ROLE_EMPLOYEE')")
    @GetMapping("{id}")
    public ResponseEntity getUserAccountById(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(userAccountService.getUserAccountById(id));
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }


    @GetMapping("username/{username}")
    public ResponseEntity getUserAccountByUsername(@PathVariable String username) {
        try{
            return ResponseEntity.ok(userAccountService.getUserAccountByUsername(username));
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }


    @PostMapping("register")
    public ResponseEntity<UserAccountResponseDTO> addUserAccount(@RequestBody UserAccountDTO userAccountDTO) {
        try {
            return ResponseEntity.status(201).body(userAccountService.addUserAccount(userAccountDTO));
        } catch (EntityNotFoundException e) {
            return this.handleException(400, e);
        }
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @DeleteMapping("{id}")
    public ResponseEntity deleteUserAccount(@PathVariable Long id) {
        try {
            UserAccount user = userAccountService.getUserAccountById(id);
            if (user.getType() != UserAccountType.ROLE_USER) {
                return ResponseEntity.status(400).body("User with accounts cannot be deleted.");
            }
            else {
                userAccountService.deleteUserAccount(id);
                return ResponseEntity.status(204).build();
            }
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

    @PreAuthorize("principal.username == @userAccountService.getUserAccountById(#id).username OR hasRole('ROLE_EMPLOYEE')")
    @PutMapping("{id}")
    public ResponseEntity<UserAccountResponseDTO> updateUserAccount(@PathVariable Long id, @RequestBody UserAccountUpdateDTO userAccountUpdateDTO) {
        try {
            return ResponseEntity.status(200).body(userAccountService.updateUserAccount(id, userAccountUpdateDTO));
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PatchMapping("{id}")
    public ResponseEntity<UserAccountResponseDTO> patchUserAccount(@PathVariable Long id, @RequestBody UserAccountPatchDTO userAccountPatchDTO) {
            return ResponseEntity.status(200).body(userAccountService.patchUserAccount(id, userAccountPatchDTO));
    }

    private ResponseEntity handleException(int status, EntityNotFoundException e) {
        ExceptionDTO dto = new ExceptionDTO(status, e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }

}
