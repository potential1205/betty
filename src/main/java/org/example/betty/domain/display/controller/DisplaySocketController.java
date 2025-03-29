package org.example.betty.domain.display.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.display.dto.Pixel;
import org.example.betty.domain.display.dto.PixelUpdateMessage;
import org.example.betty.domain.display.service.DisplayService;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DisplaySocketController {

    private final DisplayService displayService;

    @MessageMapping("/updatePixel")
    @SendTo("/topic/pixelUpdate/{gameId}/{teamId}")
    public PixelUpdateMessage updatePixel(PixelUpdateMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String walletAddress = (String) headerAccessor.getSessionAttributes().get("walletAddress");

        if (walletAddress == null || walletAddress.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_WEBSOCKET_SESSION);
        }

        Pixel pixel = new Pixel(walletAddress, message.getColor());
        displayService.updatePixel(message.getGameId(), message.getTeamId(), message.getR(), message.getC(), pixel);
        message.setWalletAddress(walletAddress);

        return message;
    }

    @MessageMapping("/getBoard/{gameId}/{teamId}")
    @SendTo("/topic/board/{gameId}/{teamId}")
    public Pixel[][] getBoard(@DestinationVariable Long gameId, @DestinationVariable Long teamId) {
        return displayService.getDisplay(gameId, teamId);
    }
}