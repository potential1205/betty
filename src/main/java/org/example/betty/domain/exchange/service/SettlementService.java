package org.example.betty.domain.exchange.service;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

public interface SettlementService {

    void createPreVoteTeamSettle(BigInteger gameId,
                    BigInteger teamAId,
                    BigInteger teamBId,
                    BigInteger startTime,
                    String teamATokenAddress,
                    String teamBTokenAddress);

    void finalizePreVoteTeamSettle(BigInteger gameId, BigInteger winningTeamId);

    List<String> getWinningTeamBettors(BigInteger gameId);

    TransactionReceipt claimForUser(BigInteger gameId, String userWalletAddress);

}
