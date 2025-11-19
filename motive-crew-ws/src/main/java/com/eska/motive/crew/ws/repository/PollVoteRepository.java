package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Poll;
import com.eska.motive.crew.ws.entity.PollVote;
import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

    Optional<PollVote> findByPollAndUser(Poll poll, User user);

    List<PollVote> findByUserAndPollIn(User user, List<Poll> polls);

    long countByPoll(Poll poll);

    @Query("SELECT COUNT(DISTINCT pv.user.id) FROM PollVote pv")
    long countDistinctUserIds();
}

