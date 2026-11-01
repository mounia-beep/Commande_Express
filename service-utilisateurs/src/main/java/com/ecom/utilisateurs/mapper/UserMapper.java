package com.ecom.utilisateurs.mapper;

import com.ecom.utilisateurs.dto.UserRequestDTO;
import com.ecom.utilisateurs.dto.UserResponseDTO;
import com.ecom.utilisateurs.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        User u = new User();
        updateEntity(u, dto);
        return u;
    }

    public void updateEntity(User u, UserRequestDTO dto) {
        u.setNom(dto.nom().trim());
        u.setEmail(dto.email().trim().toLowerCase());
        u.setRole(dto.role());
        if (dto.actif() != null) {
            u.setActif(dto.actif());
        }
    }

    public UserResponseDTO toDto(User u) {
        return new UserResponseDTO(u.getId(), u.getNom(), u.getEmail(), u.getRole(), u.isActif());
    }
}