package org.example.betty.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.ContractGasProvider;

@Component
@RequiredArgsConstructor
public class Web3ContractUtil {

    private final Web3j web3j;

    @Value("${ADMIN_PRIVATE_KEY}")
    private String adminPrivateKey;

    // 트랜잭션 매니저 반환
    public TransactionManager getTransactionManager() {
        Credentials credentials = Credentials.create(adminPrivateKey);
        return new RawTransactionManager(web3j, credentials);
    }

    // 가스 프로바이더 반환 (수수료 없으므로 기본값 사용 가능)
    public ContractGasProvider getContractGasProvider() {
        return new DefaultGasProvider();
    }
}
