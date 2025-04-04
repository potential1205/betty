package org.example.betty.domain.exchange.service;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Optional;

public interface Web3jService {
    Web3j getWeb3j();
    Credentials getCredentials();

    String callFunction(String contractAddress, String encodedFunction) throws Exception;

    TransactionReceipt sendTransaction(String contractAddress, BigInteger value, String encodedFunction) throws Exception;

    Optional<TransactionReceipt> getTransactionReceipt(String transactionHash) throws Exception;
}
