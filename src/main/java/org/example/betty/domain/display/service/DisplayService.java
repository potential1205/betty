package org.example.betty.domain.display.service;

import org.example.betty.domain.display.dto.Pixel;
import org.example.betty.domain.display.entity.Display;

import java.util.List;

public interface DisplayService {

    Pixel[][] getDisplay(String gameCode, String teamCode);

    void updatePixel(String gameCode, String teamCode, int r, int c, Pixel pixel);

    List<Display> getAllDisplayList(String accessToken);

    void checkDisplayAccess(String accessToken, String gameCode, String teamCode);

    void createDisplayAccess(String accessToken, String gameCode, String teamCode, String txHash);

    void gameEnd(String gameId, String teamCode);

    void inningEnd(String gameId, String teamCode, String inning);
}
