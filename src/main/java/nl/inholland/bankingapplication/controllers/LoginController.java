package nl.inholland.bankingapplication.controllers;

import nl.inholland.bankingapplication.models.dto.LoginDTO;
import nl.inholland.bankingapplication.models.dto.LoginResponseDTO;
import nl.inholland.bankingapplication.services.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/login")
public class LoginController {
    private UserAccountService userService;

    public LoginController(UserAccountService userService) {
        this.userService = userService;
    }
    //makes it a post request and the login responds to any post request to the login endpoint
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody LoginDTO loginDTO){
        //request body to access it
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(userService.login(loginDTO.getUsername(), loginDTO.getPassword())));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Collections.singletonMap(
                            "User credentials not valid", e.getMessage()
                    ));
        }
        /*UserAccount user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if(user != null){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.badRequest().body("Invalid credentials");
        }*/

    }
}
