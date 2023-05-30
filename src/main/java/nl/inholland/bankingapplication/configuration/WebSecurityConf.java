package nl.inholland.bankingapplication.configuration;

import nl.inholland.bankingapplication.filter.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class WebSecurityConf {

    // About the method security annotation enables the @PreAuthorize annotation for role-based security
// https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html

    private JWTFilter jwtTokenFilter;

    public WebSecurityConf(JWTFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    // To create our own custom security configuration, we create a SecurityFilterChain bean
// Read more here: https://docs.spring.io/spring-security/reference/servlet/authorization/authoize-http-requests.html
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/BankAccounts").permitAll()
                .requestMatchers("/UserAccounts").permitAll()
                .requestMatchers("/UserAccounts/{id}").permitAll()
                .requestMatchers("/UserAccounts/update/{id}").permitAll()
                .requestMatchers("/UserAccounts/registered").permitAll()
                
                .anyRequest().authenticated();

// We ensure our own filter is executed before the framework runs its own authentication filter code
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
