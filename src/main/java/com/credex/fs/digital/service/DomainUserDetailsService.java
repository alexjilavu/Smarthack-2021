package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.Authority;
import com.credex.fs.digital.domain.User;
import com.credex.fs.digital.repository.UserRepository;
import com.credex.fs.digital.security.MobileCustomer;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
            .findOneByLogin(username)
            .map(DomainUserDetailsService::createSpringSecurityUser)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private static org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        if (user.getPassword() == null) {
            throw new RuntimeException("No password set");
        }

        String authority = "ROLE_USER";

        return new MobileCustomer(
            user.getLogin(),
            user.getPassword(),
            user.getId(),
            Collections.singletonList(new SimpleGrantedAuthority(authority))
        );
    }
}
