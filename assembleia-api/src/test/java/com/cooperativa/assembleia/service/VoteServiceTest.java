package com.cooperativa.assembleia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cooperativa.assembleia.client.CpfValidationClient;
import com.cooperativa.assembleia.client.CpfValidationClient.CpfValidationResponse;
import com.cooperativa.assembleia.domain.Vote;
import com.cooperativa.assembleia.domain.Vote.VoteChoice;
import com.cooperativa.assembleia.domain.VoteRepository;
import com.cooperativa.assembleia.domain.VotingSession;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingSessionService votingSessionService;

    @Mock
    private CpfValidationClient cpfValidationClient;

    @InjectMocks
    private VoteService voteService;

    private VotingSession openSession;
    private VotingSession closedSession;

    @BeforeEach
    void setUp() {
        openSession = new VotingSession();
        openSession.setId(1L);
        openSession.setStartTime(LocalDateTime.now().minusMinutes(1));
        openSession.setEndTime(LocalDateTime.now().plusMinutes(5));

        closedSession = new VotingSession();
        closedSession.setId(2L);
        closedSession.setStartTime(LocalDateTime.now().minusMinutes(10));
        closedSession.setEndTime(LocalDateTime.now().minusMinutes(5));
    }

    @Test
    void registerVote_Success() {
        when(votingSessionService.getSessionById(1L)).thenReturn(openSession);
        when(voteRepository.existsByVotingSessionIdAndMemberId(1L, "123")).thenReturn(false);
        when(cpfValidationClient.validateCpf("123")).thenReturn(new CpfValidationResponse("ABLE_TO_VOTE"));

        voteService.registerVote(1L, "123", VoteChoice.SIM);

        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    void registerVote_SessionClosed_ThrowsException() {
        when(votingSessionService.getSessionById(2L)).thenReturn(closedSession);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            voteService.registerVote(2L, "123", VoteChoice.SIM);
        });

        assertEquals("A sessão de votação não está aberta.", ex.getMessage());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void registerVote_AlreadyVoted_ThrowsException() {
        when(votingSessionService.getSessionById(1L)).thenReturn(openSession);
        when(voteRepository.existsByVotingSessionIdAndMemberId(1L, "123")).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            voteService.registerVote(1L, "123", VoteChoice.SIM);
        });

        assertEquals("Associado já votou nesta pauta.", ex.getMessage());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void registerVote_CpfUnable_ThrowsException() {
        when(votingSessionService.getSessionById(1L)).thenReturn(openSession);
        when(voteRepository.existsByVotingSessionIdAndMemberId(1L, "123")).thenReturn(false);
        when(cpfValidationClient.validateCpf("123")).thenReturn(new CpfValidationResponse("UNABLE_TO_VOTE"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            voteService.registerVote(1L, "123", VoteChoice.SIM);
        });

        assertEquals("Associado não está habilitado para votar.", ex.getMessage());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void calculateResult_ReturnsCorrectCounts() {
        Vote v1 = new Vote(); v1.setChoice(VoteChoice.SIM);
        Vote v2 = new Vote(); v2.setChoice(VoteChoice.SIM);
        Vote v3 = new Vote(); v3.setChoice(VoteChoice.NAO);

        when(voteRepository.findByVotingSessionId(1L)).thenReturn(List.of(v1, v2, v3));

        VoteService.VotingResult result = voteService.calculateResult(1L);

        assertEquals(2, result.totalSim());
        assertEquals(1, result.totalNao());
        assertEquals(3, result.totalVotes());
    }
}
