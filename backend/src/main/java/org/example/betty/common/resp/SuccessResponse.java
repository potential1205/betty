package org.example.betty.common.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    private boolean success;

    public static SuccessResponse of(boolean success) {
        return SuccessResponse.builder()
                .success(success)
                .build();
    }
}
