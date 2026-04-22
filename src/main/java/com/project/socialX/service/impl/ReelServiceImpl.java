package com.project.socialX.service.impl;
import com.project.socialX.domain.Reel;
import com.project.socialX.domain.User;
import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.intergration.MinioChannel;
import com.project.socialX.repository.ReelCommentRepository;
import com.project.socialX.repository.ReelLikeRepository;
import com.project.socialX.repository.ReelRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.ReelService;
import com.project.socialX.service.dto.Reel.ReelRequest;
import com.project.socialX.service.dto.Reel.ReelResponse;
import com.project.socialX.service.mapper.ReelMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReelServiceImpl implements ReelService {

    private final ReelRepository reelRepository;
    private final UserRepository userRepository;
    private final ReelLikeRepository reelLikeRepository;
    private final ReelCommentRepository reelCommentRepository;
    private final ReelMapper reelMapper;
    private final MinioChannel minioChannel;


    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    private void setReelResponseStats(ReelResponse response) {
        if (response == null) return;
        
        long likeCount = reelLikeRepository.countByReelId(response.getId());
        long commentCount = reelCommentRepository.countByReelId(response.getId());
        
        response.setLikeCount(likeCount);
        response.setCommentCount(commentCount);

        String currentEmail = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentEmail != null) {
            userRepository.findByEmail(currentEmail).ifPresent(user -> {
                response.setIsLiked(reelLikeRepository.existsByReelIdAndUserId(response.getId(), user.getId()));
            });
        } else {
            response.setIsLiked(false);
        }
    }

    @Override
    @Transactional
    public ReelResponse createReel(ReelRequest request, MultipartFile videoFile) {
        User currentUser = currentUser();
        if (videoFile == null && videoFile.isEmpty()) {
            throw new BadRequestException("Bắt buộc phải có video để upload reels");
        }
        if(!videoFile.getContentType().startsWith("video")) {
            throw new BadRequestException("Reel bắt buộc phải là video");
        }
        String videoUrl = minioChannel.upload(videoFile);
        Reel reel = Reel.builder()
                .user(currentUser)
                .videoUrl(videoUrl)
                .caption(request.getCaption())
                .build();
        Reel savedReel = reelRepository.save(reel);
        return reelMapper.toResponse(savedReel);
    }

    @Override
    @Transactional(readOnly = true)
    public ReelResponse getReel(Long id) {
        Reel reel = reelRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy reel với id: " + id));
        ReelResponse response = reelMapper.toResponse(reel);
        setReelResponseStats(response);
        return response;
    }

    @Override
    @Transactional
    public ReelResponse updateReel(Long id, ReelRequest request) {
        User user = currentUser();
        Reel reel = reelRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Reel not found with id: " + id));

        if (!reel.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền sửa reel này");
        }

        if (request.getCaption() != null) {
            reel.setCaption(request.getCaption());
        }

        Reel savedReel = reelRepository.save(reel);
        return reelMapper.toResponse(savedReel);
    }

    @Override
    @Transactional
    public void deleteReel(Long id) { // Lưu ý: Hàm delete nên trả về void giống PostService
        User user = currentUser();

        Reel reel = reelRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Reel not found with id: " + id));

        if (!reel.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa reel này");
        }

        reelRepository.delete(reel);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<ReelResponse> getUserReel(Long userId, PagingRequest pagingRequest) {
        Page<Reel> reelPage = reelRepository.findByUserId(userId, pagingRequest.pageable());
        Page<ReelResponse> responsePage = reelPage.map(reel -> {
            ReelResponse res = reelMapper.toResponse(reel);
            setReelResponseStats(res);
            return res;
        });
        return PagingResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<ReelResponse> getAllReels(PagingRequest pagingRequest) {
        Page<Reel> reelPage = reelRepository.findAll(pagingRequest.pageable());
        Page<ReelResponse> responsePage = reelPage.map(reel -> {
            ReelResponse res = reelMapper.toResponse(reel);
            setReelResponseStats(res);
            return res;
        });
        return PagingResponse.from(responsePage);
    }
}
