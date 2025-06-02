package org.example.betty.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.exchange.enums.TransactionStatus;
import org.example.betty.domain.exchange.repository.TransactionRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendingTransactionUtil {

    private final TransactionRepository transactionRepository;

    public boolean hasPending(String walletAddress) {
        boolean exists = transactionRepository
                .findFirstByWallet_WalletAddressAndTransactionStatusOrderByCreatedAtDesc(
                        walletAddress,
                        TransactionStatus.PENDING
                ).isPresent();
        if (exists) {
            log.warn("[PENDING 존재] 지갑 {} -> 처리 중 트랜잭션 존재", walletAddress);
        } else {
            log.debug("[PENDING 없음] 지갑 {} -> 모든 트랜잭션 완료 상태", walletAddress);
        }
        return exists;
    }

    public void throwIfPending(String walletAddress) {
        if (hasPending(walletAddress)) {
            throw new BusinessException(ErrorCode.TRANSACTION_PENDING_EXISTS);
        }
    }
}
