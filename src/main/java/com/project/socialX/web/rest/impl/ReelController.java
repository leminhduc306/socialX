package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.dto.response.Response;
import com.project.socialX.service.ReelService;
import com.project.socialX.service.dto.Reel.ReelRequest;
import com.project.socialX.service.dto.Reel.ReelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reels")
@RequiredArgsConstructor
public class ReelController {

    private final ReelService reelService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ReelResponse>> createReel(
            @RequestPart("request") ReelRequest request,
            @RequestPart("videoFile") MultipartFile videoFile) { // Bắt buộc phải có videoFile
        ReelResponse result = reelService.createReel(request, videoFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(result));
    }

    @PutMapping("/{reelId}")
    public ResponseEntity<Response<ReelResponse>> updateReel(
            @PathVariable Long reelId,
            @RequestBody ReelRequest request) {
        ReelResponse result = reelService.updateReel(reelId, request);
        return ResponseEntity.ok(Response.ok(result));
    }

    @GetMapping("/{reelId}")
    public ResponseEntity<Response<ReelResponse>> getReelById(@PathVariable Long reelId) {
        return ResponseEntity.ok(Response.ok(reelService.getReel(reelId)));
    }

    @GetMapping
    public ResponseEntity<Response<PagingResponse<ReelResponse>>> getAllReels(
            @ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(reelService.getAllReels(pagingRequest)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response<PagingResponse<ReelResponse>>> getUserReels(
            @PathVariable Long userId,
            @ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(reelService.getUserReel(userId, pagingRequest)));
    }

    @DeleteMapping("/{reelId}")
    public ResponseEntity<Response<Void>> deleteReel(@PathVariable Long reelId) {
        reelService.deleteReel(reelId);
        return ResponseEntity.ok(Response.ok(null));
    }
}