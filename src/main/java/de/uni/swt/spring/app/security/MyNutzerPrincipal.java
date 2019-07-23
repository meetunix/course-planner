package de.uni.swt.spring.app.security;

import de.uni.swt.spring.backend.data.entity.Nutzer;
import de.uni.swt.spring.backend.data.entity.Student;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MyNutzerPrincipal implements UserDetails {
    private Nutzer user;

    public MyNutzerPrincipal(Nutzer user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities;
        if (user.getRole().equalsIgnoreCase("Student")) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("Student"));

        } else if (user.getRole().equalsIgnoreCase("Dozent")) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("Dozent"));

        } else if (user.getRole().equalsIgnoreCase("Admin")) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("Admin"));

        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("Unbekannt"));

        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswort();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
