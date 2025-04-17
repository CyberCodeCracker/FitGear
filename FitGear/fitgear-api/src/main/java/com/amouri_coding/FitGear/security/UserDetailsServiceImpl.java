package com.amouri_coding.FitGear.security;

import com.amouri_coding.FitGear.user.User;
import com.amouri_coding.FitGear.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail + " not found."));

        return user;
    }
}