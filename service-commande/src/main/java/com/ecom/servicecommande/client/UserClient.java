package com.ecom.servicecommande.client;

import com.ecom.servicecommande.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-utilisateurs")
public interface UserClient {

    @GetMapping("/users/{id}")
    UserDto getUser(@PathVariable("id") Long id);
}