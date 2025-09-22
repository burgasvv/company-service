package org.burgas.companyspringboot.entity.identity;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

    DIRECTOR,
    EMPLOYEE;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
