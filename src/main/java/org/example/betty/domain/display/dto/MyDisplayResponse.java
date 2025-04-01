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
public class MyDisplayResponse {

    private List<Display> displayList;

    public static MyDisplayResponse of(List<Display> displayList) {
        return MyDisplayResponse.builder()
                .displayList(displayList)
                .build();
    }
}
