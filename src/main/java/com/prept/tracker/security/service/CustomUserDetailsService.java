package com.prept.tracker.security.service;

import com.prept.tracker.domain.entity.User;
import com.prept.tracker.repository.UserRepository;
import com.prept.tracker.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new UsernameNotFoundException("User is disabled: " + username);
        }
        return new UserPrincipal(user);
    }
}
