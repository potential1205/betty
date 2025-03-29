package org.example.betty.domain.display.service;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.util.S3Util;
import org.example.betty.common.util.SessionUtil;
import org.example.betty.domain.display.entity.Display;
import org.example.betty.domain.display.dto.Pixel;
import org.example.betty.domain.display.entity.WalletsDisplays;
import org.example.betty.domain.display.repository.DisplayRepository;
import org.example.betty.domain.wallet.entity.Wallet;
import org.example.betty.domain.wallet.repository.WalletRepository;
import org.example.betty.domain.wallet.repository.WalletsDisplaysRepository;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisplayServiceImpl implements DisplayService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final S3Util s3Util;
    private final DisplayRepository displayRepository;
    private final WalletRepository walletRepository;
    private final WalletsDisplaysRepository walletsDisplaysRepository;
    private final SessionUtil sessionUtil;

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
        String key = "display" + ":" + gameId + ":" + teamId;

        Pixel[][] board = getDisplay(gameId, teamId);
        board[r][c] = pixel;
        redisTemplate.opsForValue().set(key, board);
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
