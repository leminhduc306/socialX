package com.project.socialX.service;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.service.dto.Post.PostRequest;
import com.project.socialX.service.dto.Post.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request, List<MultipartFile> mediaFiles);

    // Chức năng update chỉ cho phép xóa ảnh cũ (qua request.deletedMediaIds) và sửa caption
    PostResponse updatePost(Long postId, PostRequest request);

    PostResponse getPostById(Long id);

    PagingResponse<PostResponse> getAllPosts(PagingRequest pagingRequest);

    PagingResponse<PostResponse> getUserPosts(Long userId, PagingRequest pagingRequest);

    void deletePost(Long postId);
}
