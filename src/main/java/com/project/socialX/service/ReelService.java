package com.project.socialX.service;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.service.dto.Post.PostResponse;
import com.project.socialX.service.dto.Reel.ReelRequest;
import com.project.socialX.service.dto.Reel.ReelResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ReelService {
    ReelResponse createReel(ReelRequest request, MultipartFile videoFile);
    ReelResponse getReel(Long id);
    ReelResponse updateReel(Long id, ReelRequest request);
    void deleteReel(Long id);
    PagingResponse<ReelResponse> getUserReel(Long userId, PagingRequest pagingRequest);
    PagingResponse<ReelResponse> getAllReels(PagingRequest pagingRequest);

}
