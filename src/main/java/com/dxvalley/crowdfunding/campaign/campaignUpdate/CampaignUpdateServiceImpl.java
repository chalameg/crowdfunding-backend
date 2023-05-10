package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignService;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.CampaignUpdateDTO;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.CampaignUpdateMapper;
import com.dxvalley.crowdfunding.campaign.campaignUpdate.dto.CampaignUpdateResponseDTO;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignUpdateServiceImpl implements CampaignUpdateService {
    private final CampaignUpdateRepository campaignUpdateRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final UserService userService;
    private final CampaignService campaignService;

    /**
     Retrieves all campaign updates for a given campaign ID.
     @param campaignId The ID of the campaign.
     @return A list of CampaignUpdateResponseDTO objects representing the campaign updates.
     @throws ResourceNotFoundException If there are no updates for the specified campaign.
     */
    public List<CampaignUpdateResponseDTO> getAllCampaignUpdates(Long campaignId) {
        List<CampaignUpdate> campaignUpdates = campaignUpdateRepository
                .findCampaignUpdatesByCampaignCampaignId(campaignId);
        if(campaignUpdates.isEmpty())
            throw new ResourceNotFoundException("Currently, there is no update for this campaign.");

        return campaignUpdates
                .stream()
                .map(CampaignUpdateMapper::toResponseDTO)
                .toList();
    }

    /**
     Creates a campaign update using the provided CampaignUpdateDTO.
     @param campaignUpdateDTO The CampaignUpdateDTO object containing the update details.
     @return The CampaignUpdateResponseDTO object representing the created campaign update.
     */
    public CampaignUpdateResponseDTO createCampaignUpdate(CampaignUpdateDTO campaignUpdateDTO) {
        CampaignUpdate campaignUpdate = new CampaignUpdate();
        Campaign campaign = campaignService.utilGetCampaignById(campaignUpdateDTO.getCampaignId());
        Users user = userService.utilGetUserByUserId(campaignUpdateDTO.getAuthorID());

        campaignUpdate.setTitle(campaignUpdateDTO.getTitle());
        campaignUpdate.setDescription(campaignUpdateDTO.getDescription());
        campaignUpdate.setDateTime(LocalDateTime.now().format(dateTimeFormatter));
        campaignUpdate.setAuthor(user);
        campaignUpdate.setCampaign(campaign);
        CampaignUpdate savedCampaignUpdate = campaignUpdateRepository.save(campaignUpdate);

        return CampaignUpdateMapper.toResponseDTO(savedCampaignUpdate);
    }

    /**
     Updates a campaign update with the provided CampaignUpdateDTO.
     @param id The ID of the campaign update to be updated.
     @param campaignUpdateDTO The CampaignUpdateDTO object containing the update details.
     @return The CampaignUpdateResponseDTO object representing the updated campaign update.
     */
    public CampaignUpdateResponseDTO updateCampaignUpdate(Long id, CampaignUpdateDTO campaignUpdateDTO) {
        CampaignUpdate campaignUpdate = getCampaignUpdateById(id);
        Users user = userService.utilGetUserByUserId(campaignUpdateDTO.getAuthorID());

        campaignUpdate.setAuthor(user);
        campaignUpdate.setTitle(campaignUpdateDTO.getTitle());
        campaignUpdate.setDescription(campaignUpdateDTO.getDescription());
        campaignUpdate.setDateTime(LocalDateTime.now().format(dateTimeFormatter));
        CampaignUpdate updatedCampaignUpdate = campaignUpdateRepository.save(campaignUpdate);

        return CampaignUpdateMapper.toResponseDTO(updatedCampaignUpdate);
    }

    /**
     Deletes a campaign update with the specified ID.
     @param id The ID of the campaign update to be deleted.
     */
    public void deleteCampaignUpdate(Long id) {
        CampaignUpdate campaignUpdate = getCampaignUpdateById(id);
        campaignUpdateRepository.delete(campaignUpdate);
    }

    private CampaignUpdate getCampaignUpdateById(Long id) {
        return campaignUpdateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no campaign update with this ID."));

    }
}
