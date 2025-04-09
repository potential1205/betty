package org.example.betty.domain.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;


import java.math.BigInteger;
import java.util.List;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final Web3jService web3jService;

    /**
     * 게임 생성: createGame 호출
     *
     * @param gameId             게임 ID
     * @param teamAId            팀 A ID
     * @param teamBId            팀 B ID
     * @param startTime          게임 시작 시간 (Unix timestamp 등)
     * @param teamATokenAddress  팀 A 토큰 주소
     * @param teamBTokenAddress  팀 B 토큰 주소
     */
    @Override
    public void createGame(BigInteger gameId,
                                         BigInteger teamAId,
                                         BigInteger teamBId,
                                         BigInteger startTime,
                                         String teamATokenAddress,
                                         String teamBTokenAddress) {
        log.info("createGame 시작");
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.WinningTeamVoting contract = org.example.betty.contract.WinningTeamVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            log.info("createGame 호출");
            TransactionReceipt receipt = contract.createGame(gameId, teamAId, teamBId, startTime, teamATokenAddress, teamBTokenAddress)
                    .send();

            log.info("[CREATE GAME SUCCESS] gameId={}, txHash={}", gameId, receipt.getTransactionHash());

        } catch (Exception e) {
            log.error("[CREATE GAME FAILED] gameId={}, reason={}", gameId, e.getMessage(), e);
        }
    }

    /**
     * 게임 종료: finalize 호출
     *
     * @param gameId        게임 ID
     * @param winningTeamId 승리팀 ID
     * @return TransactionReceipt (성공시 트랜잭션 영수증 반환)
     */
    @Override
    public void finalize(BigInteger gameId, BigInteger winningTeamId) {
        log.info("finalize 시작");
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.WinningTeamVoting contract = org.example.betty.contract.WinningTeamVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            log.info("finalize 호출");
            TransactionReceipt receipt = contract.finalize(gameId, winningTeamId).send();

            log.info("[FINALIZE GAME SUCCESS] gameId={}, txHash={}", gameId, receipt.getTransactionHash());
        } catch (Exception e) {
            log.error("[FINALIZE GAME FAILED] gameId={}, reason={}", gameId, e.getMessage(), e);
        }
    }

    /**
     * 승리팀 베팅 사용자 목록 조회: getWinningTeamBettors 호출
     *
     * @param gameId 게임 ID
     * @return List&lt;String&gt; 승리팀에 베팅한 사용자 지갑 주소 목록
     */
    @Override
    public List<String> getWinningTeamBettors(BigInteger gameId) {
        log.info("getWinningTeamBettors 시작");
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.WinningTeamVoting contract = org.example.betty.contract.WinningTeamVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            log.info("getWinningTeamBettors 호출");
            List<String> bettors = contract.getWinningTeamBettors(gameId).send();

            log.info("[GET WINNING TEAM BETTORS SUCCESS] gameId={}, bettors={}", gameId, bettors);
            return bettors;
        } catch (Exception e) {
            log.error("[GET WINNING TEAM BETTORS FAILED] gameId={}, reason={}", gameId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 위임 정산: claimForUser 호출
     *
     * @param gameId          게임 ID
     * @param userWalletAddress 정산 받을 사용자 지갑 주소
     * @return TransactionReceipt (성공시 트랜잭션 영수증 반환)
     */
    @Override
    public void claimForUser(BigInteger gameId, String userWalletAddress) {
        log.info("claimForUser 시작");
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.WinningTeamVoting contract = org.example.betty.contract.WinningTeamVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            log.info("claimForUser 호출");

            TransactionReceipt receipt = contract.claimForUser(gameId, userWalletAddress).send();

            log.info("[CLAIM FOR USER SUCCESS] user={}, gameId={}, txHash={}", userWalletAddress, gameId, receipt.getTransactionHash());

        } catch (Exception e) {
            log.error("[CLAIM FOR USER FAILED] gameId={}, user={}, reason={}", gameId, userWalletAddress, e.getMessage(), e);
        }
    }

    // ----------------------------- mvp ---------------------------

    @Override
    public void createMVPGame(BigInteger gameId, List<BigInteger> playerIds, List<String> tokenAddresses, BigInteger startTime) {
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.MVPVoting contract = org.example.betty.contract.MVPVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            TransactionReceipt receipt = contract.createMVPGame(gameId, playerIds, tokenAddresses, startTime)
                    .send();

            log.info("[CREATE MVP GAME SUCCESS] gameId={}, txHash={}", gameId, receipt.getTransactionHash());

        } catch (Exception e) {
            log.error("[CREATE MVP GAME FAILED] gameId={}, reason={}", gameId, e.getMessage(), e);
        }
    }

    @Override
    public void finalizePreVoteMVP(BigInteger gameId, BigInteger winningPlayerId) {
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.MVPVoting contract = org.example.betty.contract.MVPVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            TransactionReceipt receipt = contract.finalizeMVP(gameId, winningPlayerId).send();

            log.info("[FINALIZE MVP GAME SUCCESS] gameId={}, txHash={}", gameId, receipt.getTransactionHash());
        } catch (Exception e) {
            log.error("[FINALIZE MVP GAME FAILED] gameId={}, reason={}", gameId, e.getMessage(), e);
        }
    }

    @Override
    public List<String> getWinningVoters(BigInteger gameId) {
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.MVPVoting contract = org.example.betty.contract.MVPVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            List<String> bettors = contract.getWinningVoters(gameId).send();

            log.info("[GET WINNING MVP BETTORS SUCCESS] gameId={}, bettors={}", gameId, bettors);
            return bettors;
        } catch (Exception e) {
            log.error("[GET WINNING MVP BETTORS FAILED] gameId={}, reason={}", gameId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void claimMVPRewardForUser(BigInteger gameId, String user) {
        try {
            Web3j web3j = web3jService.getWeb3j();
            Credentials credentials = web3jService.getCredentials();
            long chainId = web3jService.getChainId();

            org.example.betty.contract.MVPVoting contract = org.example.betty.contract.MVPVoting.load(
                    "",
                    web3j,
                    new RawTransactionManager(web3j, credentials, chainId),
                    new DefaultGasProvider()
            );

            TransactionReceipt receipt = contract.claimMVPRewardForUser(gameId, user).send();

            log.info("[CLAIM FOR USER SUCCESS] user={}, gameId={}, txHash={}", user, gameId, receipt.getTransactionHash());

        } catch (Exception e) {
            log.error("[CLAIM FOR USER FAILED] gameId={}, user={}, reason={}", gameId, user, e.getMessage(), e);
        }
    }
}

