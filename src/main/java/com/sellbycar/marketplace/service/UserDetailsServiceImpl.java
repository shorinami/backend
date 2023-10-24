package com.sellbycar.marketplace.service;


import com.sellbycar.marketplace.config.UserDetailsConfig;
import com.sellbycar.marketplace.repository.UserRepository;
import com.sellbycar.marketplace.repository.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, BadCredentialsException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) throw new BadCredentialsException("User with this email not found");
        return new UserDetailsConfig(optionalUser.get());
    }
}
