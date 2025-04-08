package org.example.betty.domain.display.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.display.dto.Pixel;
import org.example.betty.domain.display.dto.PixelUpdateMessage;
import org.example.betty.domain.display.service.DisplayService;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DisplaySocketController {

    private final DisplayService displayService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/updatePixel")
    public void updatePixel(PixelUpdateMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String walletAddress = (String) headerAccessor.getSessionAttributes().get("walletAddress");

        if (walletAddress == null || walletAddress.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_WEBSOCKET_SESSION);
        }

        Pixel pixel = new Pixel(walletAddress, message.getColor());
        displayService.updatePixel(message.getGameCode(), message.getTeamCode(), message.getR(), message.getC(), pixel);
        message.setWalletAddress(walletAddress);

        String destination = "/topic/pixelUpdate/" + message.getGameCode() + "/" + message.getTeamCode();
        messagingTemplate.convertAndSend(destination, message);
    }

    @MessageMapping("/getBoard/{gameCode}/{teamCode}")
    @SendTo("/topic/board/{gameCode}/{teamCode}")
    public Pixel[][] getBoard(@DestinationVariable String gameCode, @DestinationVariable String teamCode) {
        log.info("display 조회 요청 도착" + gameCode + "," + teamCode);
        return displayService.getDisplay(gameCode, teamCode);
    }
}