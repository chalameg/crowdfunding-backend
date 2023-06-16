package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.ProgressUpdateMapper;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.ProgressUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.ProgressUpdateResponse;
import com.dxvalley.crowdfunding.exception.customException.ForbiddenException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignProgressUpdateServiceImpl implements CampaignProgressUpdateService {
    private final CampaignProgressUpdateRepository campaignProgressUpdateRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final UserUtils userUtils;
    private final CampaignUtils campaignUtils;

    // Retrieves all campaign updates for a given campaign ID.
    public List<ProgressUpdateResponse> getAllCampaignUpdates(Long campaignId) {
        List<CampaignProgressUpdate> campaignProgressUpdates = campaignProgressUpdateRepository
                .findByCampaignId(campaignId);
        if (campaignProgressUpdates.isEmpty())
            throw new ResourceNotFoundException("Currently, there is no progress update for this campaign.");

        return campaignProgressUpdates
                .stream()
                .map(ProgressUpdateMapper::toResponseDTO)
                .toList();
    }

    // Creates a campaign progress update using the provided ProgressUpdateReq.
    @Override
    public ProgressUpdateResponse createCampaignUpdate(ProgressUpdateReq progressUpdateReq) {
        CampaignProgressUpdate campaignProgressUpdate = initializeCampaignProgressUpdate(progressUpdateReq);
        validateCampaignCreator(campaignProgressUpdate.getCampaign(), progressUpdateReq.getAuthorID());
        CampaignProgressUpdate savedCampaignProgressUpdate = campaignProgressUpdateRepository.save(campaignProgressUpdate);
        return ProgressUpdateMapper.toResponseDTO(savedCampaignProgressUpdate);
    }

    private CampaignProgressUpdate initializeCampaignProgressUpdate(ProgressUpdateReq progressUpdateReq) {
        CampaignProgressUpdate campaignProgressUpdate = new CampaignProgressUpdate();
        Campaign campaign = campaignUtils.utilGetCampaignById(progressUpdateReq.getCampaignId());
        Users user = userUtils.utilGetUserByUserId(progressUpdateReq.getAuthorID());

        campaignProgressUpdate.setTitle(progressUpdateReq.getTitle());
        campaignProgressUpdate.setDescription(progressUpdateReq.getDescription());
        campaignProgressUpdate.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
        campaignProgressUpdate.setAuthor(user);
        campaignProgressUpdate.setCampaign(campaign);

        return campaignProgressUpdate;
    }

    // Updates a campaign update with the provided ProgressUpdateReq.
    @Override
    public ProgressUpdateResponse updateCampaignUpdate(Long id, ProgressUpdateReq progressUpdateReq) {
        CampaignProgressUpdate campaignProgressUpdate = getCampaignUpdateById(id);
        validateCampaignCreator(campaignProgressUpdate.getCampaign(), progressUpdateReq.getAuthorID());
        updateCampaignProgressUpdate(campaignProgressUpdate, progressUpdateReq);
        CampaignProgressUpdate updatedCampaignProgressUpdate = campaignProgressUpdateRepository.save(campaignProgressUpdate);
        return ProgressUpdateMapper.toResponseDTO(updatedCampaignProgressUpdate);
    }


    private void validateCampaignCreator(Campaign campaign, Long authorId) {
        if (campaign.getUser().getUserId() != authorId)
            throw new ForbiddenException("This operation is only allowed for the campaign creator");
    }

    private void updateCampaignProgressUpdate(CampaignProgressUpdate campaignProgressUpdate, ProgressUpdateReq progressUpdateReq) {
        if (progressUpdateReq.getTitle() != null)
            campaignProgressUpdate.setTitle(progressUpdateReq.getTitle());
        if (progressUpdateReq.getDescription() != null)
            campaignProgressUpdate.setDescription(progressUpdateReq.getDescription());
        campaignProgressUpdate.setUpdatedAt(LocalDateTime.now().format(dateTimeFormatter));
    }

    // Deletes a campaign update with the specified ID.
    public ResponseEntity<ApiResponse> deleteCampaignUpdate(Long id) {
        getCampaignUpdateById(id);
        campaignProgressUpdateRepository.deleteById(id);

        return ApiResponse.success("Deleted Successfully");
    }

    private CampaignProgressUpdate getCampaignUpdateById(Long id) {
        return campaignProgressUpdateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no campaign progress update with this ID."));

    }
}
