package com.cooperativa.assembleia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cooperativa.assembleia.client.CpfValidationClient;
import com.cooperativa.assembleia.client.CpfValidationClient.CpfValidationResponse;
import com.cooperativa.assembleia.domain.Vote;
import com.cooperativa.assembleia.domain.VoteRepository;
import com.cooperativa.assembleia.domain.VotingSession;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingSessionService votingSessionService;

    @Mock
    private CpfValidationClient cpfValidationClient;

    @InjectMocks
    private VoteService voteService;

    private VotingSession activeSession;
    private VotingSession expiredSession;

    @BeforeEach
    void setUp() {
        activeSession = VotingSession.builder()
                .id(1L)
                .startTime(LocalDateTime.now().minusMinutes(5))
                .endTime(LocalDateTime.now().plusMinutes(5))
                .build();
                
        expiredSession = VotingSession.builder()
                .id(2L)
                .startTime(LocalDateTime.now().minusMinutes(10))
                .endTime(LocalDateTime.now().minusMinutes(5))
                .build();
    }

    @Test
    void shouldRegisterVoteSuccessfully() {
        when(votingSessionService.getSessionById(1L)).thenReturn(activeSession);
        when(voteRepository.existsByVotingSessionIdAndMemberId(1L, "12345678901")).thenReturn(false);
        when(cpfValidationClient.validateCpf("12345678901")).thenReturn(new CpfValidationResponse("ABLE_TO_VOTE"));

        voteService.registerVote(1L, "12345678901", "SIM");

        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    void shouldThrowErrorIfSessionIsClosed() {
        when(votingSessionService.getSessionById(2L)).thenReturn(expiredSession);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            voteService.registerVote(2L, "12345678901", "SIM");
        });

        assertEquals("A sessão de votação não está aberta.", exception.getMessage());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void shouldThrowErrorIfMemberAlreadyVoted() {
        when(votingSessionService.getSessionById(1L)).thenReturn(activeSession);
        when(voteRepository.existsByVotingSessionIdAndMemberId(1L, "12345678901")).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            voteService.registerVote(1L, "12345678901", "SIM");
        });

        assertEquals("Associado já votou nesta pauta.", exception.getMessage());
        verify(cpfValidationClient, never()).validateCpf(anyString());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void shouldThrowErrorIfCpfUnableToVote() {
        when(votingSessionService.getSessionById(1L)).thenReturn(activeSession);
        when(voteRepository.existsByVotingSessionIdAndMemberId(1L, "12345678901")).thenReturn(false);
        when(cpfValidationClient.validateCpf("12345678901")).thenReturn(new CpfValidationResponse("UNABLE_TO_VOTE"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            voteService.registerVote(1L, "12345678901", "SIM");
        });

        assertEquals("Associado não está habilitado para votar.", exception.getMessage());
        verify(voteRepository, never()).save(any(Vote.class));
    }
}
