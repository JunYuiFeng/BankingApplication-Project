package nl.inholland.bankingapplication.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountDTO;
import nl.inholland.bankingapplication.models.dto.UserAccountUpdateDTO;
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

    @GetMapping
    public ResponseEntity getAllUserAccounts() {
        try{
            return ResponseEntity.ok(userAccountService.getAllUserAccounts());
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

    @GetMapping("registered")
    public ResponseEntity getAllRegisteredUserAccounts() {
        try{
            return ResponseEntity.ok(userAccountService.getAllRegisteredUserAccounts());
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

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


    @PostMapping
    public ResponseEntity<UserAccount> addUserAccount(@RequestBody UserAccountDTO userAccountDTO) {
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
            userAccountService.deleteUserAccount(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<UserAccount> updateUserAccount(@PathVariable Long id, @RequestBody UserAccountUpdateDTO userAccountDTO) {
        try {
            return ResponseEntity.status(200).body(userAccountService.updateUserAccount(id, userAccountDTO));
        } catch (EntityNotFoundException e) {
            return this.handleException(404, e);
        }
    }

    private ResponseEntity handleException(int status, EntityNotFoundException e) {
        ExceptionDTO dto = new ExceptionDTO(e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }

}
