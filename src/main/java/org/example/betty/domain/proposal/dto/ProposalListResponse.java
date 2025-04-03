package org.example.betty.domain.proposal.dto;

import lombok.*;
import org.example.betty.domain.proposal.entity.Proposal;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalListResponse {

    private List<Proposal> proposalList;

    public static ProposalListResponse of(List<Proposal> proposalList) {
        return ProposalListResponse.builder()
                .proposalList(proposalList)
                .build();
    }
}
