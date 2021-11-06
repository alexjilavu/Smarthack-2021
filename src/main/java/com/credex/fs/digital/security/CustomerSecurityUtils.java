package com.credex.fs.digital.security;

import com.credex.fs.digital.domain.User;
import com.credex.fs.digital.repository.UserRepository;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CustomerSecurityUtils {

    @Autowired
    private UserRepository userRepository;

    //    public static Optional<Long> getCurrentUserId() {
    //        return extractPrincipal(SecurityContextHolder.getContext().getAuthentication()).map(MobileCustomer::getUserId);
    //    }
    //
    //    private static Optional<MobileCustomer> extractPrincipal(Authentication authentication) {
    //        if (authentication == null) {
    //            return Optional.empty();
    //        } else if (authentication.getPrincipal() instanceof MobileCustomer) {
    //            return Optional.of((MobileCustomer) authentication.getPrincipal());
    //        }
    //
    //        return Optional.empty();
    //    }

    public Optional<User> getUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(EntityNotFoundException::new);

        return userRepository.findOneByLogin(login);
    }
}
