package com.cooperativa.assembleia.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVotingSessionIdAndMemberId(Long votingSessionId, String memberId);

    List<Vote> findByVotingSessionId(Long votingSessionId);
}
