package org.example.betty.domain.display.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PixelUpdateMessage {
    private String gameCode;
    private String teamCode;
    private int r;
    private int c;
    private String walletAddress;
    private String color;
}
