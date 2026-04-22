package com.project.socialX.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reel_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "reel_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReelLike extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reel_id", nullable = false)
    private Reel reel;
}
