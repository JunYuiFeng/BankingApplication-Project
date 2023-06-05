package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.BankAccount;
import nl.inholland.bankingapplication.models.dto.BankAccounResponseDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountRegisterDTO;
import nl.inholland.bankingapplication.models.dto.BankAccountUpdateDTO;
import nl.inholland.bankingapplication.models.dto.ExceptionDTO;
import nl.inholland.bankingapplication.services.BankAccountService;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //an annotation provided by Spring MVC that combines @Controller and @ResponseBody.
// It is used to create a RESTful web service endpoint that directly returns data, rather than rendering a web page like traditional MVC controllers.
@CrossOrigin
@RequestMapping("BankAccounts")
public class BankAccountController {
    private BankAccountService bankAccountService;
    private UserAccountService userAccountService;
    private String authenticatUsername;


    public BankAccountController(BankAccountService bankAccountService, UserAccountService userAccountService) {
        this.bankAccountService = bankAccountService;
        this.userAccountService = userAccountService;
    }

//    public void  authenticatedUsername() {
//        UserAccount userAccount = userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
//        this.authenticatUsername = userAccount.getUsername();
//    }

    @GetMapping
    public ResponseEntity<List<BankAccounResponseDTO>> getAllBankAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("ExcludeUserAccount/{userId}")
    public ResponseEntity<List<BankAccount>> getAllBankAccountsExceptBankOwnAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsExceptOwnAccount(userId));
    }

    @GetMapping("{IBAN}")
    public ResponseEntity getBankAccountByIBAN(@PathVariable String IBAN) {
        return ResponseEntity.ok(bankAccountService.getBankAccountByIBAN(IBAN));
    }

    @PreAuthorize("principal.username == @userAccountService.getUserAccountById(#id).username")
    @GetMapping("UserAccount/{id}")
    public ResponseEntity<List<BankAccount>> getBankAccountByUserAccountId(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByUserAccountId(id));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping(params = "status")
    public ResponseEntity<List<BankAccount>> getBankAccountByStatus(@RequestParam String status) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByStatus(status));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping(params = "name")
    public ResponseEntity<List<BankAccount>> getBankAccountByName(@RequestParam String name) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByName(name));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping
    public ResponseEntity<BankAccounResponseDTO> addBankAccount(@RequestBody BankAccountRegisterDTO dto) {
        return ResponseEntity.status(201).body(bankAccountService.addBankAccount(dto));
    }

    @PatchMapping("/{IBAN}")
    public ResponseEntity<BankAccount> updateBankAccount(@RequestBody BankAccountUpdateDTO dto, @PathVariable String IBAN) {
        return ResponseEntity.status(201).body(bankAccountService.updateBankAccount(dto, IBAN, userAccountService.getUserAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
    }

    private ResponseEntity handleException(int status, Exception e) {
        ExceptionDTO dto = new ExceptionDTO(status, e.getClass().getName(), e.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
