package com.cooperativa.assembleia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cooperativa.assembleia.domain.Agenda;
import com.cooperativa.assembleia.domain.AgendaRepository;

@ExtendWith(MockitoExtension.class)
public class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaService agendaService;

    private Agenda testAgenda;

    @BeforeEach
    void setUp() {
        testAgenda = Agenda.builder()
                .id(1L)
                .title("Pauta de Teste")
                .description("Descrição da pauta de teste")
                .build();
    }

    @Test
    void shouldCreateAgendaSuccessfully() {
        when(agendaRepository.save(any(Agenda.class))).thenReturn(testAgenda);

        Agenda created = agendaService.createAgenda("Pauta de Teste", "Descrição da pauta de teste");

        assertNotNull(created);
        assertEquals("Pauta de Teste", created.getTitle());
        verify(agendaRepository).save(any(Agenda.class));
    }

    @Test
    void shouldThrowErrorWhenCreatingAgendaWithoutTitle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            agendaService.createAgenda("", "Descrição da pauta de teste");
        });

        assertEquals("O título da pauta é obrigatório.", exception.getMessage());
    }

    @Test
    void shouldReturnAgendaById() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(testAgenda));

        Agenda found = agendaService.getAgendaById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void shouldThrowErrorWhenAgendaNotFound() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            agendaService.getAgendaById(99L);
        });

        assertEquals("Pauta não encontrada.", exception.getMessage());
    }
}
