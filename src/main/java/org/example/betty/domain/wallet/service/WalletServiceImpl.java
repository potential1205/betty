package org.example.betty.domain.wallet.service;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final SessionUtil sessionUtil;
    private final WalletRepository walletRepository;

    @Override
    public String retrieveWallet(String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        return wallet.getWalletAddress();
    }

    @Override
    public void registerWallet(String accessToken, String nickname) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        if (walletRepository.existsByWalletAddress(walletAddress)) {
            throw new BusinessException(ErrorCode.ALREADY_EXIST_WALLET);
        }

        Wallet newWallet = Wallet.builder()
                .walletAddress(walletAddress)
                .nickname(nickname)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        walletRepository.save(newWallet);
    }
}
