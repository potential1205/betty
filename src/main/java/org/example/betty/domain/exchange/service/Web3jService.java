package org.example.betty.domain.exchange.service;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Optional;

public interface Web3jService {

    String callFunction(String contractAddress, String encodedFunction) throws Exception;

    TransactionReceipt sendTransaction(String contractAddress, BigInteger value, String encodedFunction) throws Exception;

    Optional<TransactionReceipt> getTransactionReceipt(String transactionHash) throws Exception;
}
