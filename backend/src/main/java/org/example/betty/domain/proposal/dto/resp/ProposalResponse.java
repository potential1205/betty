package org.example.betty.domain.proposal.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.betty.domain.proposal.entity.Proposal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalResponse {

    private Proposal proposal;

    public static ProposalResponse of(Proposal proposal) {
        return ProposalResponse.builder()
                .proposal(proposal)
                .build();
    }
}
