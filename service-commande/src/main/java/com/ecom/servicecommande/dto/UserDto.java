package com.ecom.servicecommande.dto;

public record UserDto(Long id, String nom, String email, String role, boolean actif) { }