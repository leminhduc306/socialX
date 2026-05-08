package com.project.socialX.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Quan hệ N-1: Nhiều bài viết có thể thuộc về 1 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "caption", columnDefinition = "TEXT")
    private String caption;

    @Builder.Default
    @Column(name = "likes_count")
    private long likesCount = 0;

    @Builder.Default
    @Column(name = "comment_count")
    private long commentCount = 0;

    // Quan hệ 1-N: 1 bài viết chứa nhiều file Media.
    // cascade = ALL: Lưu/Sửa/Xóa Post thì Hibernate tự động làm điều tương tự với
    // PostMedia
    // orphanRemoval = true: Nếu xóa 1 ảnh khỏi danh sách này, nó sẽ tự động bị xóa
    // trong Database
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC") // Tự động sắp xếp ảnh theo thứ tự khi query lên
    @Builder.Default
    private List<PostMedia> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostComment> comments = new ArrayList<>();

    public void addMedia(PostMedia media) {
        mediaList.add(media);
        media.setPost(this);
    }

    public void removeMedia(PostMedia media) {
        mediaList.remove(media);
        media.setPost(null);
    }
}