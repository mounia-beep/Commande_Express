package com.ecom.utilisateurs.dto;

import com.ecom.utilisateurs.entity.Role;

public record UserResponseDTO(Long id, String nom, String email, Role role, boolean actif) {}