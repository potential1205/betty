package org.example.betty.domain.wallet.service;

import org.example.betty.domain.wallet.entity.Wallet;

public interface WalletService {
    String retrieveWallet(String accessToken);

    void registerWallet(String accessToken, String nickname);

    Wallet findByAccessToken(String accessToken);
}
