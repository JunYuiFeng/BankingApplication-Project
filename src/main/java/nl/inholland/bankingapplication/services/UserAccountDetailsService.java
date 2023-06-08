package nl.inholland.bankingapplication.services;

import nl.inholland.bankingapplication.models.UserAccount;
import nl.inholland.bankingapplication.repositories.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements UserDetailsService {
    private UserAccountRepository userRepository;

    public UserAccountDetailsService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername: " + username);

        final UserAccount member = userRepository.findUserAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));

        System.out.println(member.getType().toString());

        return User
                .withUsername(username)
                .password(member.getPassword())
                .authorities(member.getType().toString())
                .build();
    }

}

