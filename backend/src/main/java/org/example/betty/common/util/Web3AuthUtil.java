package org.example.betty.common.util;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Component
public final class Web3AuthUtil {

    private Web3AuthUtil() {}

    public static String extractWalletAddress(List<Map<String, Object>> wallets) {
        Map<String,Object> entry = wallets.stream()
                .filter(w -> "secp256k1".equals(w.get("curve")) && "web3auth_app_key".equals(w.get("type")))
                .findFirst().orElseThrow();

        return deriveAddressFromCompressedKey(entry.get("public_key").toString());
    }

    public static String deriveAddressFromCompressedKey(String publicKeyHex) {
        byte[] compressed = HexFormat.of().parseHex(publicKeyHex);

        ECPoint point = ECNamedCurveTable
                .getParameterSpec("secp256k1")
                .getCurve()
                .decodePoint(compressed);

        byte[] uncompressed = point.getEncoded(false);
        byte[] pubBytes = new byte[64];
        System.arraycopy(uncompressed, 1, pubBytes, 0, 64);

        BigInteger pubKeyBI = new BigInteger(1, pubBytes);
        return "0x" + Keys.getAddress(pubKeyBI);
    }
}
