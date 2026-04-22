package com.project.socialX.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMedia extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Chiều ngược lại của quan hệ 1-N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "media_url", nullable = false, columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(name = "media_type", length = 20)
    private String mediaType; // Sẽ lưu 'image' hoặc 'video'

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 1;
}