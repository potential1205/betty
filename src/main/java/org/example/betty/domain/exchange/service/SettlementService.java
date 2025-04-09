package org.example.betty.domain.exchange.service;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

public interface SettlementService {

    void createGame(BigInteger gameId,
                    BigInteger teamAId,
                    BigInteger teamBId,
                    BigInteger startTime,
                    String teamATokenAddress,
                    String teamBTokenAddress);

    void finalizeGame(BigInteger gameId, BigInteger winningTeamId);

    List<String> getWinningTeamBettors(BigInteger gameId);

    TransactionReceipt claimForUser(BigInteger gameId, String userWalletAddress);

}
