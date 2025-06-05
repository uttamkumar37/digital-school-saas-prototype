package com.digitalschool.user.service;

import com.digitalschool.user.entity.User;
import com.digitalschool.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // For simplicity, tenantId can come from username parsing or token, here we assume username includes tenant info, or can be improved
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Modify if tenantId is passed separately
        var userOpt = userRepository.findByUsernameAndTenant_TenantId(extractUsername(username), extractTenantId(username));
        User user = userOpt.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    private String extractUsername(String combined) {
        // Assuming username format: tenantId|username, split by |
        if (combined.contains("|")) return combined.split("\\|")[1];
        return combined;
    }

    private String extractTenantId(String combined) {
        if (combined.contains("|")) return combined.split("\\|")[0];
        return "defaultTenant"; // or throw exception
    }
}
