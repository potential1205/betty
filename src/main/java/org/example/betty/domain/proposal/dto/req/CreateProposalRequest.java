package org.example.betty.domain.proposal.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProposalRequest {
    private Long teamId;
    private String title;
    private String content;
    private int targetCount;
}
