package org.example.betty.domain.exchange.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Web3jServiceImpl implements Web3jService {

    private final Web3j web3j;

    @Value("${admin.private.key}")
    private String adminPrivateKey;

    private Credentials credentials;

    @Override
    public Web3j getWeb3j() {
        return web3j;
    }

    @Override
    public Credentials getCredentials() {
        return Credentials.create(adminPrivateKey);
    }


    @PostConstruct
    private void init() {
        credentials = Credentials.create(adminPrivateKey);
    }
    @Override
    public String callFunction(String contractAddress, String encodedFunction) throws Exception {
        Transaction transaction = Transaction.createEthCallTransaction(
                credentials.getAddress(), contractAddress, encodedFunction
        );
        EthCall response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
        return response.getValue();
    }

    @Override
    public TransactionReceipt sendTransaction(String contractAddress, BigInteger value, String encodedFunction) throws Exception {
        RawTransactionManager txManager = new RawTransactionManager(web3j, credentials, 14609);
        EthSendTransaction transaction = txManager.sendTransaction(
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                contractAddress,
                encodedFunction,
                value
        );

        String txHash = transaction.getTransactionHash();
        Optional<TransactionReceipt> receiptOptional = waitForTransactionReceipt(txHash);

        return receiptOptional.orElseThrow(() -> new RuntimeException("Transaction receipt not found"));
    }

    @Override
    public Optional<TransactionReceipt> getTransactionReceipt(String transactionHash) throws Exception {
        return web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();
    }

    // 트랜잭션 Receipt polling
    private Optional<TransactionReceipt> waitForTransactionReceipt(String transactionHash) throws Exception {
        int attempts = 40;
        int sleepDuration = 1500; // 1.5초 간격

        for (int i = 0; i < attempts; i++) {
            Optional<TransactionReceipt> receipt = web3j.ethGetTransactionReceipt(transactionHash)
                    .send()
                    .getTransactionReceipt();

            if (receipt.isPresent()) {
                return receipt;
            }

            Thread.sleep(sleepDuration);
        }

        return Optional.empty();
    }
}
