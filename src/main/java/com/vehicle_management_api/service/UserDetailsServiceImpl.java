package com.vehicle_management_api.service;

import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client = clientRepository.findByEmailAndActive(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado com email: " + email));

        return new User(
                client.getEmail(),
                client.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + client.getUserType().name()))
        );
    }
}
