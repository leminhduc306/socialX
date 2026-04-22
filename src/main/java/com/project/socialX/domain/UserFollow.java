package com.project.socialX.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_follows", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"follower_id", "following_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollow extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower; // Người bấm follow

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following; // Người được follow
}
