package com.ecom.servicecommande.client;

import com.ecom.servicecommande.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class FakeUserClient implements UserClient {
    @Override
    public UserDto getUser(Long id) {
        return new UserDto(id, "Utilisateur test", "test@mail.com", "CLIENT", true);
    }
}