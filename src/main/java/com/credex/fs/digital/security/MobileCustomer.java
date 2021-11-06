package com.credex.fs.digital.security;

import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
@EqualsAndHashCode
public class MobileCustomer extends User {

    private final Long userId;

    public MobileCustomer(String username, String password, Long userId, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }
}
