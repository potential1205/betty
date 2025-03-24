package org.example.betty.domain.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String idToken;
}
