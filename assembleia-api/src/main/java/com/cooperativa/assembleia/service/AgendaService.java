package com.cooperativa.assembleia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cooperativa.assembleia.domain.Agenda;
import com.cooperativa.assembleia.domain.AgendaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public Agenda createAgenda(String title, String description) {
        Agenda agenda = Agenda.builder()
            .title(title)
            .description(description)
            .build();
        return agendaRepository.save(agenda);
    }

    public List<Agenda> getAllAgendas() {
        return agendaRepository.findAll();
    }

    public Agenda getAgendaById(Long id) {
        return agendaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pauta não encontrada."));
    }
}
