package org.example.betty.domain.display.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.common.util.S3Util;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.display.entity.Display;
import org.example.betty.domain.display.dto.Pixel;
import org.example.betty.domain.display.entity.DisplayAccess;
import org.example.betty.domain.display.repository.DisplayAccessRepository;
import org.example.betty.domain.display.repository.DisplayRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisplayServiceImpl implements DisplayService{

    @Qualifier("redisTemplate3")
    private final RedisTemplate<String, Object> redisTemplate3;
    private final SimpMessagingTemplate messagingTemplate;
    private final S3Util s3Util;
    private final DisplayRepository displayRepository;
    private final DisplayAccessRepository displayAccessRepository;
    private final WalletRepository walletRepository;
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
    public void checkDisplayAccess(String accessToken, Long gameId, Long teamId) {
        String walletAddress = sessionUtil.getWalletAddress(accessToken);

        Wallet wallet = walletRepository.findByWalletAddress(walletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WALLET));

        if (!displayAccessRepository.existsByWalletAddressAndGameIdAndTeamId(wallet.getWalletAddress(), gameId, teamId)) {
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

            // 정보 출력
            String findTxHash = tx.getHash();
            String from = tx.getFrom(); // 지갑 주소
            String to = tx.getTo(); // 컨트랙트 주소

            String input = tx.getInput();
            String tokenAddress = "0x" + input.substring(34, 74); // 토큰 주소
            Long findGameId = Long.parseLong(String.valueOf(Long.valueOf(input.substring(74, 138), 16))); // 게임 id
            Long findTeamId = Long.parseLong(String.valueOf(Long.valueOf(input.substring(138, 202), 16))); // 팀 ID

            log.info("findTxHash: {}", findTxHash);
            log.info("walletAddress(from): {}", from); // 지갑 주소
            log.info("contractAddress(to): {}", to); // 컨트랙트 주소

            log.info("Input Data: {}", input);
            log.info("tokenAddress: {}", tokenAddress);
            log.info("findGameId: {}", findGameId);
            log.info("findTeamId: {}", findTeamId);

            if (!walletAddress.equalsIgnoreCase(wallet.getWalletAddress())) {
                throw new BusinessException(ErrorCode.INVALID_TRANSACTION);
            }

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FAILED_CONNECT_WEB3J);
        }

        DisplayAccess displayAccess = DisplayAccess.builder()
                .walletAddress(wallet.getWalletAddress())
                .gameId(gameId)
                .teamId(teamId)
                .build();

        displayAccessRepository.save(displayAccess);
    }

    @Override
    public void gameEnd(Long gameId, Long teamId) {
        String destination = "/topic/gameEnd/" + gameId + "/" + teamId;
        messagingTemplate.convertAndSend(destination, "GAME_OVER");
        String key = "display" + ":" + gameId + ":" + teamId;
        redisTemplate3.delete(key);
        log.info("게임이 정상적으로 종료되었습니다. {},{}", gameId, teamId);
    }

    @Override
    public void inningEnd(Long gameId, Long teamId, int inning) {
        String key = "display" + ":" + gameId + ":" + teamId;
        Pixel[][] display = (Pixel[][]) redisTemplate3.opsForValue().get(key);
        saveDisplay(display, gameId, teamId, inning);
        log.info("이닝이 정상적으로 종료되었습니다. {},{}", gameId, teamId);
    }

    @Override
    public Pixel[][] getDisplay(Long gameId, Long teamId) {
        String key = "display" + ":" + gameId + ":" + teamId;

        log.info("조회된 전광판 key : " + key);

        Pixel[][] board = (Pixel[][]) redisTemplate3.opsForValue().get(key);
        if (board == null) {
            log.info("전광판이 조회되지 않아 새로 생성합니다. : " + key);
            board = new Pixel[64][64];
            for (int i = 0; i < 64; i++) {
                for (int j = 0; j < 64; j++) {
                    board[i][j] = new Pixel("", "#ffffff");
                }
            }
            redisTemplate3.opsForValue().set(key, board);
        }
        return board;
    }

    @Override
    public void updatePixel(Long gameId, Long teamId, int r, int c, Pixel pixel) {
        String key = "display" + ":" + gameId + ":" + teamId;
        Pixel[][] board = getDisplay(gameId, teamId);
        board[r][c] = pixel;
        redisTemplate3.opsForValue().set(key, board);
        log.info("updatePixcel 성공" + pixel.getColor() + pixel.getWalletAddress() + " " + r + " " + c);
    }

//    public void saveDisplay(Pixel[][] display, Long gameId, Long teamId, int inning) {
//        BufferedImage image = createImageFromBoard(display);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        try {
//            ImageIO.write(image, "png", baos);
//            baos.flush();
//            byte[] imageBytes = baos.toByteArray();
//            baos.close();
//
//            String contentType = "image/png";
//            String displayUrl = s3Util.upload(imageBytes, contentType, gameId, teamId, inning);
//
//            Display newDisplay = Display.builder()
//                    .gameId(gameId)
//                    .teamId(teamId)
//                    .inning(inning)
//                    .displayUrl(displayUrl)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            log.info("게임ID : " + gameId + "팀 ID : "  + teamId + " " + inning + " 번째 이닝 이미지가 정상적으로 저장되었습니다.");
//            displayRepository.save(newDisplay);
//        } catch (IOException e) {
//            throw new BusinessException(ErrorCode.DISPLAY_SAVE_FAILED);
//        }
//    }

    public void saveDisplay(Pixel[][] display, Long gameId, Long teamId, int inning) {
        // 1. board 배열로부터 원본 이미지를 생성 (예: 64x64)
        BufferedImage originalImage = createImageFromBoard(display);

        // 2. 업스케일링 배율 (예: 4배)
        final int UPSCALE_FACTOR = 4;
        BufferedImage upscaledImage = upscaleImage(originalImage, UPSCALE_FACTOR);

        // 3. 업스케일된 이미지를 ByteArray로 변환 (try-with-resources 사용)
        byte[] imageBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // ImageIO.write는 이미지 작성에 실패하면 false를 반환할 수 있음
            if (!ImageIO.write(upscaledImage, "png", baos)) {
                throw new BusinessException(ErrorCode.DISPLAY_SAVE_FAILED);
            }
            baos.flush();
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            log.error("이미지 변환 중 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.DISPLAY_SAVE_FAILED);
        }

        // 4. S3에 업스케일된 이미지 업로드
        String contentType = "image/png";
        String displayUrl = s3Util.upload(imageBytes, contentType, gameId, teamId, inning);

        // 5. Display 엔티티 생성 및 저장
        Display newDisplay = Display.builder()
                .gameId(gameId)
                .teamId(teamId)
                .inning(inning)
                .displayUrl(displayUrl)
                .createdAt(LocalDateTime.now())
                .build();

        displayRepository.save(newDisplay);

        log.info("게임ID: {} 팀ID: {} {}번째 이닝 이미지가 정상적으로 저장되었습니다.", gameId, teamId, inning);
    }

    private BufferedImage upscaleImage(BufferedImage image, int scaleFactor) {
        int scaledWidth = image.getWidth() * scaleFactor;
        int scaledHeight = image.getHeight() * scaleFactor;

        // 이미지 타입이 0이면 기본 TYPE_INT_ARGB로 지정
        int imageType = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage upscaledImage = new BufferedImage(scaledWidth, scaledHeight, imageType);

        Graphics2D g2d = upscaledImage.createGraphics();
        // Nearest Neighbor 보간법 적용: 픽셀 아트의 날카로운 경계를 유지
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return upscaledImage;
    }

    public BufferedImage createImageFromBoard(Pixel[][] display) {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);

        for (int r = 0; r < 64; r++) {
            for (int c = 0; c < 64; c++) {
                Pixel pixel = display[r][c];
                Color color = Color.decode(pixel.getColor());
                image.setRGB(c, r, color.getRGB());
            }
        }
        return image;
    }

}
