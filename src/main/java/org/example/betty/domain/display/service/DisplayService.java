package org.example.betty.domain.display.service;

import org.example.betty.domain.display.dto.Pixel;
import org.example.betty.domain.display.entity.Display;

import java.util.List;

public interface DisplayService {

    Pixel[][] getDisplay(Long gameId, Long teamId);

    void updatePixel(Long gameId, Long teamId, int r, int c, Pixel pixel);

    List<Display> getAllDisplayList(String accessToken);

    List<Display> getAllMyDisplayList(String accessToken);

    void checkDisplayAccess(String accessToken, Long gameId, Long teamId);

    void createDisplayAccess(String accessToken, Long gameId, Long teamId, String txHash);

    void gameEnd(String accessToken, Long gameId, Long teamId);

    void inningEnd(String accessToken, Long gameId, Long teamId, int inning);
}
