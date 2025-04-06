package org.example.betty.domain.proposal.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveProposalHashRequest {
    private Long proposalId;
    private String txHash;
}
