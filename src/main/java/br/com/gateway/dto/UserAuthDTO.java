package br.com.gateway.dto;

import br.com.gateway.enumeration.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {

    private UUID identifier;

    private UserType userType;

    private String accessToken;
}
