package org.example.betty.domain.exchange.service;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Optional;

public class Web3jServiceImpl implements Web3jService {

    @Override
    public String callFunction(String contractAddress, String encodedFunction) throws Exception {

        return null;
    }

    @Override
    public TransactionReceipt sendTransaction(String contractAddress, BigInteger value, String encodedFunction) throws Exception {
        return null;
    }

    @Override
    public Optional<TransactionReceipt> getTransactionReceipt(String transactionHash) throws Exception {
        return Optional.empty();
    }
}
