package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Poll;
import com.eska.motive.crew.ws.enums.PollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    List<Poll> findAllByOrderByCreatedAtDesc();

    List<Poll> findByStatusOrderByCreatedAtDesc(PollStatus status);

    List<Poll> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String search);

    List<Poll> findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(PollStatus status, String search);

    long countByStatus(PollStatus status);
}

