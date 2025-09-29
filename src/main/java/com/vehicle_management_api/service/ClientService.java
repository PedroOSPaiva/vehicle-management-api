package com.vehicle_management_api.service;

import com.vehicle_management_api.dto.ClientDTO;
import com.vehicle_management_api.entity.Client;
import com.vehicle_management_api.repository.ClientRepository;
import jakarta.persistence.Cacheable;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private static final Logger logger = LogManager.getLogger(ClientService.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Cacheable(value = "clients", key = "#id")
    public Optional<ClientDTO> findById(Long id) {
        logger.info("Buscando cliente por ID: {}", id);
        return clientRepository.findById(id).map(ClientDTO::new);
    }

    @Cacheable(value = "clients")
    public List<ClientDTO> findAll() {
        logger.info("Buscando todos os clientes");
        return clientRepository.findAll().stream()
                .map(ClientDTO::new)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "clients", allEntries = true)
    public ClientDTO create(ClientDTO clientDTO) {
        logger.info("Criando novo cliente com email: {}", clientDTO.getEmail());

        if (clientRepository.existsByEmail(clientDTO.getEmail())) {
            logger.warn("Cliente com email {} já existe", clientDTO.getEmail());
            throw new RuntimeException("Cliente com este email já existe");
        }

        Client client = new Client();
        client.setName(clientDTO.getName());
        client.setEmail(clientDTO.getEmail());
        client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        client.setUserType(clientDTO.getUserType() != null ?
                clientDTO.getUserType() : Client.userType.NORMAL_USER);

        Client savedClient = clientRepository.save(client);
        logger.info("Cliente criado com sucesso com ID: {}", savedClient.getId());
        return new ClientDTO(savedClient);
    }

    @CacheEvict(value = "clients", allEntries = true)
    public Optional<ClientDTO> update(Long id, ClientDTO clientDTO) {
        logger.info("Atualizando cliente com ID: {}", id);
        return clientRepository.findById(id).map(existingClient -> {
            existingClient.setName(clientDTO.getName());
            if (clientDTO.getPassword() != null && !clientDTO.getPassword().isEmpty()) {
                existingClient.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
            }
            Client updatedClient = clientRepository.save(existingClient);
            logger.info("Cliente com ID {} atualizado com sucesso", id);
            return new ClientDTO(updatedClient);
        });
    }

    @CacheEvict(value = "clients", allEntries = true)
    public boolean delete(Long id) {
        logger.info("Excluindo cliente com ID: {}", id);
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            logger.info("Cliente com ID {} excluído com sucesso", id);
            return true;
        }
        logger.warn("Cliente com ID {} não encontrado para exclusão", id);
        return false;
    }

    public Optional<Client> findByEmail(String email) {
        logger.debug("Buscando cliente por email: {}", email);
        return clientRepository.findByEmailAndActive(email);
    }
}
