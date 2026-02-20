package com.cooperativa.assembleia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cooperativa.assembleia.domain.Agenda;
import com.cooperativa.assembleia.service.AgendaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agenda createAgenda(@RequestBody AgendaRequest request) {
        return agendaService.createAgenda(request.title(), request.description());
    }

    @GetMapping
    public List<Agenda> getAllAgendas() {
        return agendaService.getAllAgendas();
    }

    @GetMapping("/{id}")
    public Agenda getAgenda(@PathVariable Long id) {
        return agendaService.getAgendaById(id);
    }

    public record AgendaRequest(String title, String description) {}
}
