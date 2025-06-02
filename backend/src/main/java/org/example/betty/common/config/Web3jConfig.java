package org.example.betty.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    @Value("${stadium.url}")
    private String stadiumUrl;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(stadiumUrl));
    }
}
