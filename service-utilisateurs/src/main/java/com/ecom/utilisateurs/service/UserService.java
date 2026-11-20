package com.ecom.utilisateurs.service;

import com.ecom.utilisateurs.dto.UserRequestDTO;
import com.ecom.utilisateurs.dto.UserResponseDTO;
import com.ecom.utilisateurs.entity.User;
import com.ecom.utilisateurs.exception.DuplicateResourceException;
import com.ecom.utilisateurs.exception.ResourceNotFoundException;
import com.ecom.utilisateurs.mapper.UserMapper;
import com.ecom.utilisateurs.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        if (repository.existsByEmailIgnoreCase(dto.email().trim())) {
            throw new DuplicateResourceException("Email déjà utilisé : " + dto.email());
        }
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        return mapper.toDto(getOrThrow(id));
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = getOrThrow(id);
        repository.findByEmailIgnoreCase(dto.email().trim())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(o -> {
                    throw new DuplicateResourceException("Email déjà utilisé : " + dto.email());
                });
        mapper.updateEntity(user, dto);
        return mapper.toDto(repository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private User getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }
}