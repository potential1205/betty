package org.example.betty.domain.wallet.service;

public interface WalletService {
    String retrieveWallet(String accessToken);

    void registerWallet(String accessToken, String nickname);
}
