package org.example.betty.domain.proposal.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProposalResponse {

    private Long proposalId;
    private String contentHash;

    public static CreateProposalResponse of(Long proposalId, String contentHash) {
        return CreateProposalResponse.builder()
                .proposalId(proposalId)
                .contentHash(contentHash)
                .build();
    }
}
