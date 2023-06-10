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
        return ResponseEntity.ok(userAccountService.getAllUserAccounts());
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("Exclude/{id}")
    public ResponseEntity<List<UserAccountResponseDTO>> getAllUserAccountsExceptOne(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.getAllUserAccountsExceptOne(id));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("registered")
    public ResponseEntity<List<UserAccountResponseDTO>> getAllRegisteredUserAccounts() {
        return ResponseEntity.ok(userAccountService.getAllRegisteredUserAccounts());
    }

    @PreAuthorize("principal.username == @userAccountService.getUserAccountById(#id).username || hasRole('ROLE_EMPLOYEE')")
    @GetMapping("{id}")
    public ResponseEntity getUserAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.getUserAccountById(id));
    }


    @GetMapping("username/{username}")
    public ResponseEntity getUserAccountByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userAccountService.getUserAccountByUsername(username));
    }


    @PostMapping("register")
    public ResponseEntity<UserAccountResponseDTO> addUserAccount(@RequestBody UserAccountDTO userAccountDTO) {
        return ResponseEntity.status(201).body(userAccountService.addUserAccount(userAccountDTO));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @DeleteMapping("{id}")
    public ResponseEntity deleteUserAccount(@PathVariable Long id) {
        userAccountService.deleteUserAccount(id);
        return ResponseEntity.status(204).build();
    }

    @PreAuthorize("principal.username == @userAccountService.getUserAccountById(#id).username OR hasRole('ROLE_EMPLOYEE')")
    @PutMapping("{id}")
    public ResponseEntity<UserAccountResponseDTO> updateUserAccount(@PathVariable Long id, @RequestBody UserAccountUpdateDTO userAccountUpdateDTO) {
        return ResponseEntity.status(200).body(userAccountService.updateUserAccount(id, userAccountUpdateDTO));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PatchMapping("{id}")
    public ResponseEntity<UserAccountResponseDTO> patchUserAccount(@PathVariable Long id, @RequestBody UserAccountPatchDTO userAccountPatchDTO) {
            return ResponseEntity.status(200).body(userAccountService.patchUserAccount(id, userAccountPatchDTO));
    }
}
