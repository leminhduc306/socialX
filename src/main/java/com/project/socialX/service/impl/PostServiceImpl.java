package com.project.socialX.service.impl;

import com.project.socialX.domain.Post;
import com.project.socialX.domain.PostMedia;
import com.project.socialX.domain.User;
import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.intergration.MinioChannel;
import com.project.socialX.repository.PostRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.PostService;
import com.project.socialX.service.dto.Post.PostRequest;
import com.project.socialX.service.dto.Post.PostResponse;
import com.project.socialX.service.mapper.PostMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final MinioChannel minioChannel;

    /**
     * Lấy User hiện tại đang đăng nhập
     */
    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    /**
     * Hàm helper để lưu danh sách file thành danh sách PostMedia
     */
    private List<PostMedia> processMediaFiles(List<MultipartFile> mediaFiles, Post post) {
        List<PostMedia> mediaList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(mediaFiles)) {
            for (int i = 0; i < mediaFiles.size(); i++) {
                MultipartFile file = mediaFiles.get(i);
                if (file != null && !file.isEmpty()) {
                    String mediaUrl = minioChannel.upload(file);
                    
                    // Kiểm tra định dạng (ví dụ cơ bản)
                    String contentType = file.getContentType();
                    String mediaType = "image";
                    if (contentType != null && contentType.startsWith("video")) {
                        mediaType = "video";
                    }

                    PostMedia postMedia = PostMedia.builder()
                            .mediaUrl(mediaUrl)
                            .mediaType(mediaType)
                            .displayOrder(i + 1)
                            .post(post)
                            .build();

                    mediaList.add(postMedia);
                }
            }
        }
        return mediaList;
    }

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, List<MultipartFile> mediaFiles) {
        User user = currentUser();

        // Khởi tạo Post
        Post post = Post.builder()
                .user(user)
                .caption(request.getCaption() != null ? request.getCaption() : "")
                .build();

        // Xử lý Media files
        List<PostMedia> mediaList = processMediaFiles(mediaFiles, post);
        
        // Thêm vào danh sách để Cascade Type.ALL có thể lưu tự động
        mediaList.forEach(post::addMedia);

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request) {
        User user = currentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found with id: " + postId));

        // Kiểm tra quyền: Chỉ chủ bài viết mới được sửa
        if (!post.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa bài viết này");
        }

        // Cập nhật Caption
        if (request.getCaption() != null) {
            post.setCaption(request.getCaption());
        }

        // Yêu cầu xóa các hình ảnh đã chọn (nếu có)
        if (!CollectionUtils.isEmpty(request.getDeletedMediaIds())) {
            List<PostMedia> mediaToRemove = post.getMediaList().stream()
                    .filter(media -> request.getDeletedMediaIds().contains(media.getId()))
                    .toList();
            // Xóa khỏi post. Vì orphanRemoval=true, JPA sẽ tự động xóa bản ghi trong DB
            mediaToRemove.forEach(post::removeMedia);
        }

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Post not found with id: " + id));
        return postMapper.toResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<PostResponse> getAllPosts(PagingRequest pagingRequest) {
        Page<Post> postPage = postRepository.findAll(pagingRequest.pageable());

        // Map Page<Post> sang Page<PostResponse> (Mặc dù PagingResponse.from chỉ nhận Content của Page cũng chạy được)
        Page<PostResponse> responsePage = postPage.map(postMapper::toResponse);
        return PagingResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<PostResponse> getUserPosts(Long userId, PagingRequest pagingRequest) {
        Page<Post> postPage = postRepository.findByUserId(userId, pagingRequest.pageable());
        Page<PostResponse> responsePage = postPage.map(postMapper::toResponse);
        return PagingResponse.from(responsePage);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        User user = currentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found with id: " + postId));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa bài viết này");
        }

        postRepository.delete(post);
    }
}
