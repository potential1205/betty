package org.example.betty.domain.auth.dto.req;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String idToken;
}
