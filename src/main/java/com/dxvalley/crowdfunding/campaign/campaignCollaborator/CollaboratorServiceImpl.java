package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaborationRequest;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorMapper;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.messageManager.email.EmailBuilder;
import com.dxvalley.crowdfunding.messageManager.email.EmailServiceImpl;
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
public class CollaboratorServiceImpl implements CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final UserUtils userUtils;
    private final EmailServiceImpl emailService;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignUtils campaignUtils;
    private final String invitationLink = "http://10.100.51.60/invitationDetail/";

    public List<CollaboratorResponse> getCollaboratorByCampaignId(Long campaignId) {
        List<Collaborator> collaborators = this.collaboratorRepository.findCollaboratorsByCampaignIdAndAccepted(campaignId, true);
        return collaborators.stream().map(CollaboratorMapper::toCollResponse).toList();
    }

    public CollaboratorResponse getCollaboratorById(Long id) {
        return CollaboratorMapper.toCollResponse(this.utilGetCollaboratorById(id));
    }

    public CollaboratorResponse sendInvitation(CollaborationRequest collaborationRequest) {
        Long campaignId = collaborationRequest.getCampaignId();
        String inviteeEmail = collaborationRequest.getInviteeEmail();
        this.checkIfAlreadyCollaborator(campaignId, inviteeEmail);
        Users invitee = this.userUtils.utilGetUserByUsername(inviteeEmail);
        this.userUtils.verifyUser(invitee);
        this.userUtils.checkAccountActive(invitee);
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        String inviter = campaign.getUser().getFullName();
        LocalDateTime invitationSentAt = LocalDateTime.now();
        LocalDateTime expiredAt = invitationSentAt.plusDays(1L);
        Collaborator collaborator = Collaborator.builder().invitee(invitee).campaign(campaign).invitationSentAt(invitationSentAt.format(this.dateTimeFormatter)).invitationExpiredAt(expiredAt.format(this.dateTimeFormatter)).build();
        Collaborator savedCollaborator = (Collaborator)this.collaboratorRepository.save(collaborator);
        String invitationDetailLink = "http://10.100.51.60/invitationDetail/" + savedCollaborator.getId();
        this.emailService.send(inviteeEmail, EmailBuilder.buildCollaborationInvitationEmail(invitee.getFullName(), inviter, campaign.getTitle(), invitationDetailLink), "Asking for Collaboration");
        return CollaboratorMapper.toCollResponse(savedCollaborator);
    }

    private void checkIfAlreadyCollaborator(Long campaignId, String username) {
        boolean isCollaborator = this.collaboratorRepository.existsByCampaignIdAndInviteeUsername(campaignId, username);
        if (isCollaborator) {
            throw new ResourceAlreadyExistsException("An invitation has already been sent to this user for this campaign.");
        }
    }

    public ResponseEntity<ApiResponse> respondToCollaborationInvitation(Long collaboratorId, boolean accepted) {
        Collaborator collaborator = this.utilGetCollaboratorById(collaboratorId);
        LocalDateTime expiredAt = LocalDateTime.parse(collaborator.getInvitationExpiredAt(), this.dateTimeFormatter);
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This invitation has already expired.");
        } else {
            collaborator.setRespondedAt(LocalDateTime.now().format(this.dateTimeFormatter));
            collaborator.setAccepted(accepted);
            this.collaboratorRepository.save(collaborator);
            return accepted ? ApiResponse.success("Hooray! You have successfully become a collaborator.") : ApiResponse.success("Collaboration was successfully rejected.");
        }
    }

    public ResponseEntity<ApiResponse> deleteCollaborator(Long CollaboratorId) {
        this.utilGetCollaboratorById(CollaboratorId);
        this.collaboratorRepository.deleteById(CollaboratorId);
        return ApiResponse.success("Collaborator successfully deleted");
    }

    public Collaborator utilGetCollaboratorById(Long CollaboratorId) {
        return this.collaboratorRepository.findById(CollaboratorId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no Collaborator with this Id");
        });
    }
}
