package com.cooperativa.assembleia.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cooperativa.assembleia.client.CpfValidationClient;
import com.cooperativa.assembleia.domain.Vote;
import com.cooperativa.assembleia.domain.Vote.VoteChoice;
import com.cooperativa.assembleia.domain.VoteRepository;
import com.cooperativa.assembleia.domain.VotingSession;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionService votingSessionService;
    private final CpfValidationClient cpfValidationClient;

    public void registerVote(Long sessionId, String memberId, VoteChoice choice) {
        VotingSession session = votingSessionService.getSessionById(sessionId);

        // 1. Check if session is open
        if (!session.isOpen()) {
            throw new IllegalStateException("A sessão de votação não está aberta.");
        }

        // 2. Check if member already voted in this session
        if (voteRepository.existsByVotingSessionIdAndMemberId(sessionId, memberId)) {
            throw new IllegalStateException("Associado já votou nesta pauta.");
        }

        // 3. Validate CPF (Bonus Task 1)
        CpfValidationClient.CpfValidationResponse cpfResponse = cpfValidationClient.validateCpf(memberId);
        if ("UNABLE_TO_VOTE".equals(cpfResponse.getStatus())) {
            throw new IllegalStateException("Associado não está habilitado para votar.");
        }

        // 4. Register vote
        Vote vote = Vote.builder()
            .votingSession(session)
            .memberId(memberId)
            .choice(choice)
            .build();

        voteRepository.save(vote);
    }

    public VotingResult calculateResult(Long sessionId) {
        List<Vote> votes = voteRepository.findByVotingSessionId(sessionId);
        
        long totalSim = votes.stream().filter(v -> v.getChoice() == VoteChoice.SIM).count();
        long totalNao = votes.stream().filter(v -> v.getChoice() == VoteChoice.NAO).count();

        return new VotingResult(sessionId, totalSim, totalNao, totalSim + totalNao);
    }

    public record VotingResult(Long sessionId, long totalSim, long totalNao, long totalVotes) {}
}
