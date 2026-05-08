package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.dto.response.Response;
import com.project.socialX.service.PostService;
import com.project.socialX.service.dto.Post.PostRequest;
import com.project.socialX.service.dto.Post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<PostResponse>> createPost(
            @RequestPart("request") PostRequest request,
            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {
        PostResponse result = postService.createPost(request, mediaFiles);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(result));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest request) {
        PostResponse result = postService.updatePost(postId, request);
        return ResponseEntity.ok(Response.ok(result));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(Response.ok(postService.getPostById(postId)));
    }

    @GetMapping
    public ResponseEntity<Response<PagingResponse<PostResponse>>> getAllPosts(@ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(postService.getAllPosts(pagingRequest)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response<PagingResponse<PostResponse>>> getUserPosts(
            @PathVariable Long userId,
            @ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(postService.getUserPosts(userId, pagingRequest)));
    }

    @GetMapping("/feed")
    public ResponseEntity<Response<PagingResponse<PostResponse>>> getFeed(
            @RequestParam(required = false) Long lastId,
            @ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(postService.getFeed(lastId, pagingRequest)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(Response.ok(null));
    }
}
