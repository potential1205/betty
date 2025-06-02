package org.example.betty.domain.display.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDisplayAccessRequest {

    @Schema(description = "트랜잭션 해시")
    private String txHash;
}
