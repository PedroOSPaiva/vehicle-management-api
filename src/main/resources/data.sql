-- =============================================================================
-- DDL (Data Definition Language) - Vehicle Management Database
-- =============================================================================
-- AVISO IMPORTANTE:
-- Este script contém comandos MySQL específicos. Para ambientes de teste (H2),
-- utilize o schema automático do Spring Boot ou adapte os comandos para H2.
-- =============================================================================

-- COMENTADO: Criação do database (executada manualmente ou via configuração)
-- EM AMBIENTES DE TESTE H2: Este comando causará erro de sintaxe
-- EM PRODUÇÃO: Execute manualmente ou via script de deployment
-- CREATE DATABASE IF NOT EXISTS vehicle_management;
-- USE vehicle_management;

-- =============================================================================
-- TABELA: clients
-- Propósito: Armazenar informações dos clientes/usuários do sistema
-- =============================================================================
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('ADMIN', 'NORMAL_USER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- =============================================================================
-- TABELA: vehicles
-- Propósito: Armazenar informações dos veículos cadastrados no sistema
-- =============================================================================
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(30),
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    price DECIMAL(10,2),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (created_by) REFERENCES clients(id)
);

-- =============================================================================
-- TABELA: refresh_tokens
-- Propósito: Armazenar tokens de refresh para autenticação JWT
-- =============================================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    client_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- =============================================================================
-- ÍNDICES para melhor performance
-- =============================================================================
CREATE INDEX IF NOT EXISTS idx_client_email ON clients(email);
CREATE INDEX IF NOT EXISTS idx_vehicle_brand_model ON vehicles(brand, model);
CREATE INDEX IF NOT EXISTS idx_vehicle_license_plate ON vehicles(license_plate);
CREATE INDEX IF NOT EXISTS idx_refresh_token_token ON refresh_tokens(token);

-- =============================================================================
-- INSTRUÇÕES DE USO:
-- 1. PRODUÇÃO (MySQL): Execute este script completo (descomente CREATE DATABASE se necessário)
-- 2. TESTES (H2): Use apenas as tabelas - Spring Boot gerencia o schema automaticamente
-- 3. DESENVOLVIMENTO: Configure spring.jpa.hibernate.ddl-auto=update no application.properties
-- =============================================================================