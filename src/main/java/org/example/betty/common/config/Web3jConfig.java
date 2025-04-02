package org.example.betty.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.nio.file.Paths;

@Configuration
public class Web3jConfig {

    @Value("${web3j.client-address}")
    private String clientAddress;

    @Value("${wallet.password}")
    private String walletPassword;

    @Value("${wallet.keystore-path}")
    private String walletPath;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(clientAddress));
    }

    @Bean
    public Credentials credentials() throws Exception {
        return WalletUtils.loadCredentials(walletPassword, Paths.get(walletPath).toFile());
    }
}
