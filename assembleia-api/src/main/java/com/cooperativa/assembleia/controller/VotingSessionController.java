package com.cooperativa.assembleia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cooperativa.assembleia.domain.VotingSession;
import com.cooperativa.assembleia.service.VotingSessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VotingSession openSession(@RequestBody OpenSessionRequest request) {
        return votingSessionService.openSession(request.agendaId(), request.durationMinutes());
    }

    @GetMapping("/{id}")
    public VotingSession getSession(@PathVariable Long id) {
        return votingSessionService.getSessionById(id);
    }

    @GetMapping("/agenda/{agendaId}")
    public VotingSession getSessionByAgenda(@PathVariable Long agendaId) {
        return votingSessionService.getSessionByAgendaId(agendaId);
    }

    public record OpenSessionRequest(Long agendaId, Integer durationMinutes) {}
}
