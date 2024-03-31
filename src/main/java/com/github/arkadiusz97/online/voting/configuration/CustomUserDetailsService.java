package com.github.arkadiusz97.online.voting.configuration;

import com.github.arkadiusz97.online.voting.domain.User;
import com.github.arkadiusz97.online.voting.domain.UserRole;
import com.github.arkadiusz97.online.voting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(final String email) {
        User user = userRepository.findByEmail(email);
        List<User> users = userRepository.findAll();
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), true, true, true,
            true, getAuthorities(user.getUsersRoles()));
    }

    private List<SimpleGrantedAuthority> getAuthorities(final Collection<UserRole> userRoles) {
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        userRoles.forEach(userRole -> {
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName()));
        });
        return authorities;
    }

}
