package org.example.betty.domain.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckWalletNicknameResponse {
    private String nickname;

    public static CheckWalletNicknameResponse of(String nickname) {
        return CheckWalletNicknameResponse.builder().nickname(nickname).build();
    }
}
