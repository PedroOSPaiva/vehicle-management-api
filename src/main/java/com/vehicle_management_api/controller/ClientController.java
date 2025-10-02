package com.vehicle_management_api.controller;

import com.vehicle_management_api.dto.ClientDTO;
import com.vehicle_management_api.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/admin/clients")
@Tag(name = "Clientes", description = "APIs de gerenciamento de clientes (Somente administrador)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class ClientController {

    private static final Logger logger = LogManager.getLogger(ClientController.class);

    private final ClientService clientService;

    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @GetMapping
    @Operation(summary = "Obter todos os clientes (Somente administrador)")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        logger.info("Buscando todos os clientes");
        ClientService clientService = new ClientService();
        List<ClientDTO> clients = clientService.findAll();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter cliente por ID (Somente administrador)")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        logger.info("Buscando cliente por ID: {}", id);
        ClientService clientService = new ClientService();
        Optional<ClientDTO> client = clientService.findById(id);
        return client.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um cliente (Somente administrador)")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id,
                                                  @Valid @RequestBody ClientDTO clientDTO) {
        logger.info("Atualizando cliente com ID: {}", id);
        ClientService clientService = new ClientService();
        Optional<ClientDTO> updatedClient = clientService.update(id, clientDTO);
        return updatedClient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um cliente (Somente administrador)")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        logger.info("Excluindo cliente com ID: {}", id);
        ClientService clientService = new ClientService();
        boolean deleted = clientService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
