package com.cooperativa.assembleia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cooperativa.assembleia.domain.Vote.VoteChoice;
import com.cooperativa.assembleia.service.VoteService;
import com.cooperativa.assembleia.service.VoteService.VotingResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerVote(@RequestBody VoteRequest request) {
        voteService.registerVote(request.sessionId(), request.memberId(), request.choice());
    }

    @GetMapping("/session/{sessionId}/result")
    public VotingResult getResult(@PathVariable Long sessionId) {
        return voteService.calculateResult(sessionId);
    }

    public record VoteRequest(Long sessionId, String memberId, VoteChoice choice) {}
}
