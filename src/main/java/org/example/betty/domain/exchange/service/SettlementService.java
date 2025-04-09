package org.example.betty.domain.exchange.service;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

public interface SettlementService {

    // team
    void createGame(BigInteger gameId,
                    BigInteger teamAId,
                    BigInteger teamBId,
                    BigInteger startTime,
                    String teamATokenAddress,
                    String teamBTokenAddress);

    void finalize(BigInteger gameId, BigInteger winningTeamId);

    List<String> getWinningTeamBettors(BigInteger gameId);

    void claimForUser(BigInteger gameId, String userWalletAddress);

    // mvp
    void createMVPGame(BigInteger gameId, List<BigInteger> playerIds, List<String> tokenAddresses, BigInteger startTime);

    void finalizePreVoteMVP(BigInteger gameId, BigInteger winningPlayerId);

    List<String> getWinningVoters(BigInteger gameId);

    void claimMVPRewardForUser(BigInteger gameId, String user);
}
