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
    private final String invitationLink = "http://10.1.177.121:3000/invitationDetail/";

    @Override
    public List<CollaboratorResponse> getCollaboratorByCampaignId(Long campaignId) {
        List<Collaborator> collaborators = collaboratorRepository
                .findCollaboratorsByCampaignIdAndAccepted(campaignId, true);

        return collaborators.stream().map(CollaboratorMapper::toCollResponse).toList();
    }

    @Override
    public CollaboratorResponse getCollaboratorById(Long id) {
        return CollaboratorMapper.toCollResponse(utilGetCollaboratorById(id));
    }


    @Override
    public CollaboratorResponse sendInvitation(CollaborationRequest collaborationRequest) {
        Long campaignId = collaborationRequest.getCampaignId();
        String inviteeEmail = collaborationRequest.getInviteeEmail();
        checkIfAlreadyCollaborator(campaignId, inviteeEmail);

        Users invitee = userUtils.utilGetUserByUsername(inviteeEmail);
        userUtils.verifyUser(invitee);
        userUtils.checkAccountActive(invitee);

        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);
        String inviter = campaign.getUser().getFullName();

        LocalDateTime invitationSentAt = LocalDateTime.now();
        LocalDateTime expiredAt = invitationSentAt.plusDays(1);

        Collaborator collaborator = Collaborator.builder()
                .invitee(invitee)
                .campaign(campaign)
                .invitationSentAt(invitationSentAt.format(dateTimeFormatter))
                .invitationExpiredAt(expiredAt.format(dateTimeFormatter))
                .build();

        Collaborator savedCollaborator = collaboratorRepository.save(collaborator);

        String invitationDetailLink = invitationLink + savedCollaborator.getId();

        emailService.send(
                inviteeEmail,
                EmailBuilder.buildCollaborationInvitationEmail(
                        invitee.getFullName(),
                        inviter,
                        campaign.getTitle(),
                        invitationDetailLink),
                "Asking for Collaboration");

        return CollaboratorMapper.toCollResponse(savedCollaborator);
    }

    private void checkIfAlreadyCollaborator(Long campaignId, String username) {
        boolean isCollaborator = collaboratorRepository.existsByCampaignIdAndInviteeUsername(campaignId, username);
        if (isCollaborator)
            throw new ResourceAlreadyExistsException("An invitation has already been sent to this user for this campaign.");
    }

    @Override
    public ResponseEntity<ApiResponse> respondToCollaborationInvitation(Long collaboratorId, boolean accepted) {
        Collaborator collaborator = utilGetCollaboratorById(collaboratorId);

        LocalDateTime expiredAt = LocalDateTime.parse(collaborator.getInvitationExpiredAt(), dateTimeFormatter);

        if (expiredAt.isBefore(LocalDateTime.now()))
            throw new BadRequestException("This invitation has already expired.");

        collaborator.setRespondedAt(LocalDateTime.now().format(dateTimeFormatter));
        collaborator.setAccepted(accepted);
        collaboratorRepository.save(collaborator);

        if (accepted)
            return ApiResponse.success("Hooray! You have successfully become a collaborator.");
        else
            return ApiResponse.success("Collaboration was successfully rejected.");

    }

    @Override
    public ResponseEntity<ApiResponse> deleteCollaborator(Long CollaboratorId) {
        utilGetCollaboratorById(CollaboratorId);
        collaboratorRepository.deleteById(CollaboratorId);
        return ApiResponse.success("Collaborator successfully deleted");
    }

    public Collaborator utilGetCollaboratorById(Long CollaboratorId) {
        return collaboratorRepository.findById(CollaboratorId).orElseThrow(
                () -> new ResourceNotFoundException("There is no Collaborator with this Id")
        );
    }
}
