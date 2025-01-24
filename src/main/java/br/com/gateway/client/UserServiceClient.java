package br.com.gateway.client;

import br.com.gateway.dto.UserAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserServiceClient {

    @GetMapping("/users/auth/validate")
    UserAuthDTO validateTokenAndRole(@RequestHeader("Authorization") String token);
}
