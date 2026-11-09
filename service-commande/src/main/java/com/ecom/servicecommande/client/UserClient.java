package com.ecom.servicecommande.client;

import com.ecom.servicecommande.dto.UserDto;

public interface UserClient {
    UserDto getUser(Long id);
}