package org.example.betty.domain.proposal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.betty.common.resp.SuccessResponse;
import org.example.betty.domain.proposal.dto.ProposalListResponse;
import org.example.betty.domain.proposal.dto.ProposalResponse;
import org.example.betty.domain.proposal.dto.TeamTokenCountResponse;
import org.example.betty.domain.proposal.dto.req.CreateProposalRequest;
import org.example.betty.domain.proposal.dto.req.CreateWalletProposalRequest;
import org.example.betty.domain.proposal.entity.Proposal;
import org.example.betty.domain.proposal.service.ProposalService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
@Tag(name = "팀 채널 (안건)", description = "안건 조회 및 안건 둥록")
public class ProposalController {

    private final ProposalService proposalService;

    @Operation(summary = "안건 등록", description = "안건을 생성합니다.")
    @PostMapping
    public ResponseEntity<SuccessResponse> createProposal(
            @RequestBody CreateProposalRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        proposalService.createProposal(request, accessToken);

        return ResponseEntity.ok(
                SuccessResponse.of(true)
        );
    }

    @Operation(summary = "안건 투표", description = "안건에 투표합니다.")
    @PostMapping("/vote")
    public ResponseEntity<SuccessResponse> createWalletProposal(
            @RequestBody CreateWalletProposalRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        proposalService.createWalletProposal(request, accessToken);

        return ResponseEntity.ok(
                SuccessResponse.of(true)
        );
    }

    @Operation(summary = "팀 토큰 개수 조회", description = "현재 사용자가 보유한 팀 토큰 개수를 조회합니다.")
    @GetMapping("/team/{teamId}/token/count")
    public ResponseEntity<TeamTokenCountResponse> getTeamTokenCount(
            @PathVariable("teamId") Long teamId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        BigDecimal teamTokenCount = proposalService.getTeamTokenCount(teamId, accessToken);

        return ResponseEntity.ok(
                TeamTokenCountResponse.of(teamTokenCount)
        );
    }

    @Operation(summary = "안건 목록 조회", description = "전체 안건을 조회합니다.")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<ProposalListResponse> getProposalList(
            @PathVariable("teamId") Long teamId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        List<Proposal> proposalList = proposalService.getProposalList(teamId, accessToken);

        return ResponseEntity.ok(
                ProposalListResponse.of(proposalList)
        );
    }

    @Operation(summary = "안건 상세 조회", description = "단일 안건 정보를 조회합니다.")
    @GetMapping("/{proposalId}/team/{teamId}")
    public ResponseEntity<ProposalResponse> getProposal(
            @PathVariable("teamId") Long teamId,
            @PathVariable("proposalId") Long proposalId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {

        Proposal proposal = proposalService.getProposal(teamId, proposalId, accessToken);

        return ResponseEntity.ok(
                ProposalResponse.of(proposal)
        );
    }
}
