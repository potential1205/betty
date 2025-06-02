package org.example.betty.common.resolver;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FanTokenAddressResolver {
    @Value("${DSB_ADDRESS}")
    private String dsbAddress;
    @Value("${LGT_ADDRESS}")
    private String lgtAddress;
    @Value("${LTG_ADDRESS}")
    private String ltgAddress;
    @Value("${KWH_ADDRESS}")
    private String kwhAddress;
    @Value("${NCD_ADDRESS}")
    private String ncdAddress;
    @Value("${KTW_ADDRESS}")
    private String ktwAddress;
    @Value("${SSG_ADDRESS}")
    private String ssgAddress;
    @Value("${KIA_ADDRESS}")
    private String kiaAddress;
    @Value("${SSL_ADDRESS}")
    private String sslAddress;
    @Value("${HWE_ADDRESS}")
    private String hweAddress;

    private final Map<String, String> fanTokenAddressMap = new HashMap<>();

    @PostConstruct
    public void init() {
        fanTokenAddressMap.put("DSB", dsbAddress);
        fanTokenAddressMap.put("LGT", lgtAddress);
        fanTokenAddressMap.put("LTG", ltgAddress);
        fanTokenAddressMap.put("KWH", kwhAddress);
        fanTokenAddressMap.put("NCD", ncdAddress);
        fanTokenAddressMap.put("KTW", ktwAddress);
        fanTokenAddressMap.put("SSG", ssgAddress);
        fanTokenAddressMap.put("KIA", kiaAddress);
        fanTokenAddressMap.put("SSL", sslAddress);
        fanTokenAddressMap.put("HWE", hweAddress);
    }

    public String getAddress(String tokenName) {
        return fanTokenAddressMap.get(tokenName);
    }

    public boolean hasToken(String tokenName) {
        return fanTokenAddressMap.containsKey(tokenName);
    }
}
