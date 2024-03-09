package com.github.arkadiusz97.online.voting.dto;

import com.github.arkadiusz97.online.voting.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; //todo implement later
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //todo implement later
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; //todo implement later
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; //todo implement later
    }

    @Override
    public boolean isEnabled() {
        return true; //todo implement later
    }

    public User getUser() {
        return user;
    }
}
