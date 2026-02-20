package com.cooperativa.assembleia.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {
    
    @Query("SELECT vs FROM VotingSession vs WHERE vs.agenda.id = :agendaId")
    Optional<VotingSession> findByAgendaId(Long agendaId);
}
