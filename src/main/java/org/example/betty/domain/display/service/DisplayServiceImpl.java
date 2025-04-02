package org.example.betty.domain.display.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.util.S3Util;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.display.entity.Display;
import org.example.betty.domain.display.dto.Pixel;
import org.example.betty.domain.display.entity.DisplayAccess;
import org.example.betty.domain.display.entity.WalletsDisplays;
import org.example.betty.domain.display.repository.DisplayAccessRepository;
import org.example.betty.domain.display.repository.DisplayRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.domain.wallet.repository.WalletsDisplaysRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisplayServiceImpl implements DisplayService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final S3Util s3Util;
    private final DisplayRepository displayRepository;
    private final DisplayAccessRepository displayAccessRepository;
    private final WalletRepository walletRepository;
    private final WalletsDisplaysRepository walletsDisplaysRepository;
    private final SessionUtil sessionUtil;
    private final Web3j web3j;

    @Override
    public List<Display> getAllDisplayList(String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        return displayRepository.findAll();
    }

    @Override
    public List<Display> getAllMyDisplayList(String accessToken) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        List<WalletsDisplays> walletsDisplaysList = walletsDisplaysRepository.findAllByWalletId(wallet.getId());

        List<Long> displayIdList = walletsDisplaysList.stream()
                .map(WalletsDisplays::getDisplayId)
                .toList();

        return displayRepository.findByIdIn(displayIdList);
    }

    @Override
    public void checkDisplayAccess(String accessToken, Long gameId, Long teamId) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        if (!displayAccessRepository.existsByWalletAddressAndGameIdAndTeamId(wallet.getWalletAddress(),gameId, teamId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_DISPLAY_ACCESS);
        }
    }

    @Override
    public void createDisplayAccess(String accessToken, Long gameId, Long teamId, String txHash) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        if (displayAccessRepository.existsByWalletAddressAndGameIdAndTeamId(walletAddress, gameId, teamId)) {
            throw new BusinessException(ErrorCode.ALREADY_HAS_DISPLAY_ACCESS);
        }

        try {
            TransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send()
                    .getTransactionReceipt()
                    .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_CONFIRMED));

            if (!"0x1".equals(receipt.getStatus())) {
                throw new BusinessException(ErrorCode.FAILED_TRANSACTION);
            }

            Transaction tx = web3j.ethGetTransactionByHash(txHash).send()
                    .getTransaction()
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TRANSACTION));

//            if (!tx.getTo().equalsIgnoreCase(TOKEN_CONTRACT_ADDRESS)) {
//                throw new IllegalArgumentException("토큰 컨트랙트 주소가 아님");
//            }

            String input = tx.getInput();
            if (!input.startsWith("0xa9059cbb")) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN_TRANSFER);
            }

            String toAddress = "0x" + input.substring(34, 74);
            String amountHex = input.substring(74);
            BigInteger amount = new BigInteger(amountHex, 16);

            BigInteger requiredAmount = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();

            if (!toAddress.equalsIgnoreCase(wallet.getWalletAddress())) {
                throw new BusinessException(ErrorCode.INVALID_TRANSACTION);
            }

            if (amount.compareTo(requiredAmount) < 0) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_TOKEN_AMOUNT);
            }
        } catch (IOException e) {
            throw new RuntimeException("Web3 통신 실패", e);
        }

        DisplayAccess displayAccess = DisplayAccess.builder()
                .walletAddress(wallet.getWalletAddress())
                .gameId(gameId)
                .teamId(teamId)
                .build();

        displayAccessRepository.save(displayAccess);
    }

    public Pixel[][] getDisplay(Long gameId, Long teamId) {
        String key = "display" + ":" + gameId + ":" + teamId;

        Pixel[][] board = (Pixel[][]) redisTemplate.opsForValue().get(key);
        if (board == null) {
            board = new Pixel[64][64];
            for (int i = 0; i < 64; i++) {
                for (int j = 0; j < 64; j++) {
                    board[i][j] = new Pixel("", "ffffff");
                }
            }
            redisTemplate.opsForValue().set(key, board);
        }
        return board;
    }

    public void updatePixel(Long gameId, Long teamId, int r, int c, Pixel pixel) {
        log.info("updatePixel 진입");

        String key = "display" + ":" + gameId + ":" + teamId;

        Pixel[][] board = getDisplay(gameId, teamId);
        board[r][c] = pixel;
        redisTemplate.opsForValue().set(key, board);
        log.info("updatePixcel 성공" + pixel.getColor() + pixel.getWalletAddress() + " " + r + " " + c);
    }

    public void handleGameEnd(Long gameId, Long teamId) {
        String destination = "/topic/gameEnd/" + gameId + "/" + teamId;
        messagingTemplate.convertAndSend(destination, "GAME_OVER");
        String key = "display" + ":" + gameId + ":" + teamId;
        redisTemplate.delete(key);
    }

    public void handleInningEnd(Long gameId, Long teamId, int inning) {
        String key = "display" + ":" + gameId + ":" + teamId;

        Pixel[][] display = (Pixel[][]) redisTemplate.opsForValue().get(key);
        saveDisplay(display, gameId, teamId, inning);
    }

    public void saveDisplay(Pixel[][] display, Long gameId, Long teamId, int inning) {
        BufferedImage image = createImageFromBoard(display);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            String contentType = "image/png";
            String displayUrl = s3Util.upload(imageBytes, contentType, gameId, teamId, inning);

            Display newDisplay = Display.builder()
                    .gameId(gameId)
                    .teamId(teamId)
                    .inning(inning)
                    .displayUrl(displayUrl)
                    .createdAt(LocalDateTime.now())
                    .build();

            Display resultDisplay = displayRepository.save(newDisplay);
            saveWalletsDisplays(display,resultDisplay.getId());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.DISPLAY_SAVE_FAILED);
        }

    }

    public BufferedImage createImageFromBoard(Pixel[][] display) {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);

        for (int r = 0; r < 64; r++) {
            for (int c = 0; c < 64; c++) {
                Pixel pixel = display[r][c];
                Color color = Color.decode("#" + pixel.getColor());
                image.setRGB(r, c, color.getRGB());
            }
        }
        return image;
    }

    public void saveWalletsDisplays(Pixel[][] display, Long displayId) {
        Set<Long> walletIds = new HashSet<>();

        for (int r = 0; r < 64; r++) {
            for (int c = 0; c < 64; c++) {
                String walletAddress = display[r][c].getWalletAddress();
                if (walletAddress != null && !walletAddress.isEmpty()) {
                    Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));
                    walletIds.add(wallet.getId());
                }
            }
        }

        List<WalletsDisplays> walletsDisplaysList = walletIds.stream()
                .map(walletId -> WalletsDisplays.builder()
                        .walletId(walletId)
                        .displayId(displayId)
                        .build())
                .toList();

        walletsDisplaysRepository.saveAll(walletsDisplaysList);
    }

}
