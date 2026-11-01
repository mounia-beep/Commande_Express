package com.ecom.utilisateurs.dto;

import com.ecom.utilisateurs.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide") String email,
        @NotNull(message = "Le rôle est obligatoire") Role role,
        Boolean actif
) {}