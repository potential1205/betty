package org.example.betty.domain.display.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.betty.domain.display.entity.Display;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayResponse {

    private List<Display> displayList;

    public static DisplayResponse of(List<Display> displayList) {
        return DisplayResponse.builder()
                .displayList(displayList)
                .build();
    }
}
