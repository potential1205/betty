package org.example.betty.domain.proposal.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProposalResponse {

    private Long proposalId;
    private String contentHash;
    private LocalDateTime closedAt;

    public static CreateProposalResponse of(Long proposalId, String contentHash, LocalDateTime closedAt) {
        return CreateProposalResponse.builder()
                .proposalId(proposalId)
                .contentHash(contentHash)
                .closedAt(closedAt)
                .build();
    }
}
