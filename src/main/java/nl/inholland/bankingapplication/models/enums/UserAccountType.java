package nl.inholland.bankingapplication.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserAccountType implements GrantedAuthority {
    ROLE_CUSTOMER,
    ROLE_EMPLOYEE,
    ROLE_USER,
    DEACTIVATED_USER;



    @Override
    public String getAuthority() {
        return name();
    }
}

