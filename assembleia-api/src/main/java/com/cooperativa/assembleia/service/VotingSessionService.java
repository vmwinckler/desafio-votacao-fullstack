package com.cooperativa.assembleia.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.cooperativa.assembleia.domain.Agenda;
import com.cooperativa.assembleia.domain.VotingSession;
import com.cooperativa.assembleia.domain.VotingSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final AgendaService agendaService;

    public VotingSession openSession(Long agendaId, Integer durationInMinutes) {
        // Validate if a session already exists for this agenda
        votingSessionRepository.findByAgendaId(agendaId).ifPresent(session -> {
            throw new IllegalStateException("Já existe uma sessão de votação para esta pauta.");
        });

        Agenda agenda = agendaService.getAgendaById(agendaId);
        int duration = durationInMinutes != null && durationInMinutes > 0 ? durationInMinutes : 1;
        LocalDateTime now = LocalDateTime.now();

        VotingSession session = VotingSession.builder()
            .agenda(agenda)
            .startTime(now)
            .endTime(now.plusMinutes(duration))
            .build();

        return votingSessionRepository.save(session);
    }

    public VotingSession getSessionById(Long id) {
        return votingSessionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sessão de votação não encontrada."));
    }

    public VotingSession getSessionByAgendaId(Long agendaId) {
        return votingSessionRepository.findByAgendaId(agendaId).orElse(null);
    }
}
