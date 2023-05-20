package nl.inholland.bankingapplication.services;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;

    public MyUserDetailsService(UserAccountRepository repo) {
        this.userAccountRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> user1 = Optional.ofNullable(userAccountRepository.findUserAccountByUsername(username)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserAccount user = user1.get();
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(user.getType())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }


}
