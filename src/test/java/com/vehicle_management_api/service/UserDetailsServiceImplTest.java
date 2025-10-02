package com.vehicle_management_api.service;

import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.enums.UserType;
import com.vehicle_management_api.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setEmail("test@email.com");
        client.setPassword("encodedPassword");
        client.setName("Test User");
        client.setIsActive(true);
        client.setUserType(UserType.ADMIN);
    }

    @Test
    void shouldLoadUserByUsername() {
        when(clientRepository.findByEmailAndActive("test@email.com")).thenReturn(Optional.of(client));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@email.com");

        assertNotNull(userDetails);
        assertEquals("test@email.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        verify(clientRepository).findByEmailAndActive("test@email.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(clientRepository.findByEmailAndActive("nonexistent@email.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@email.com");
        });

        assertEquals("Cliente não encontrado com email: nonexistent@email.com", exception.getMessage());
        verify(clientRepository).findByEmailAndActive("nonexistent@email.com");
    }
}