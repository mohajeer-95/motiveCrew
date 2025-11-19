package com.eska.motive.crew.ws.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "poll_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @Column(nullable = false, length = 200)
    private String label;

    @Column(name = "position_index")
    private Integer position;

    @Column(name = "votes_count", nullable = false)
    @Builder.Default
    private Integer votesCount = 0;
}

