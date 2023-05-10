package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.messageManager.email.EmailBuilder;
import com.dxvalley.crowdfunding.messageManager.email.EmailServiceImpl;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.user.dto.InviteRequest;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


//TODO: update current impl
@Service
@RequiredArgsConstructor
public class CollaboratorServiceImpl implements CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final UserService userService;
    private final EmailBuilder emailBuilder;
    private final EmailServiceImpl emailService;
    private final CampaignRepository campaignRepository;
    private final DateTimeFormatter dateTimeFormatter;
    @Override
    public List<Collaborator> getCollaborators() {
        var result = collaboratorRepository.findAll();
        if(result.size()==0){
            throw new ResourceNotFoundException("Currently, There is no collaborators");
        }
        return result;
    }

    @Override
    public ResponseEntity sendInvitation(InviteRequest inviteRequest) {
        var invitee = userService.utilGetUserByUsername(inviteRequest.getUsername());
        var campaign = campaignRepository.findCampaignByCampaignId(inviteRequest.getCampaignId()).get();
        var inviteSender = userService.getUserByUsername(campaign.getOwner());

        LocalDateTime invitationSentAt = LocalDateTime.now();
        LocalDateTime expiredAt = invitationSentAt.plusDays(1);

        Collaborator collaborator = new Collaborator();
        collaborator.setCampaign(campaign);
        collaborator.setUsers(invitee);
        collaborator.setInvitationSentAt(invitationSentAt.format(dateTimeFormatter));
        collaborator.setExpiredAt(expiredAt.format(dateTimeFormatter));
        collaborator.setIsAccepted(false);

        var result = collaboratorRepository.save(collaborator);

        String link = "http://10.1.177.121:3000/invitationDetail/" + result.getCollaboratorId();

        emailService.send(
                inviteRequest.getUsername(),
                emailBuilder.emailBuilderForCollaborationInvitation(
                        invitee.getFullName(),
                        inviteSender.getFullName(),
                        campaign.getTitle(),
                        link),
                "Asking for Collaboration");
        return ApiResponse.success("The invitation sent successfully!");
    }

    @Override
    public List<Collaborator> getCollaboratorByCampaignId(Long campaignId) {
        campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );
        var collaborator = collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
        if (collaborator.isEmpty()) {
            throw new ResourceNotFoundException("There is no collaborators for this campaign");
        }
        return collaborator;
    }

    @Override
    public Collaborator getCollaboratorById(Long CollaboratorId) {
        return collaboratorRepository.findCollaboratorByCollaboratorId(CollaboratorId).orElseThrow(
                () -> new ResourceNotFoundException("There is no Collaborator with this Id")
        );
    }

    @Override
    public String deleteCollaborator(Long CollaboratorId) {
        var collaborator = getCollaboratorById(CollaboratorId);
        collaboratorRepository.delete(collaborator);
        return "Collaborator successfully deleted";
    }

    @Override
    public ResponseEntity acceptOrRejectInvitation(Long collaboratorId, Boolean flag) {

        Collaborator collaborator = getCollaboratorById(collaboratorId);
        LocalDateTime expiredAt = LocalDateTime.parse(collaborator.getExpiredAt(), dateTimeFormatter);

        if (collaborator.getRespondedAt() != null) {
            throw new ResourceAlreadyExistsException("You have already responded to this invitation.");
        }

        LocalDateTime respondedAt = LocalDateTime.now();
        collaborator.setRespondedAt(respondedAt.format(dateTimeFormatter));

        if (expiredAt.isBefore(LocalDateTime.now())){
            collaboratorRepository.save(collaborator);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "This invitation has already expired.");
        }

        if(flag){
            collaborator.setIsAccepted(true);
            collaboratorRepository.save(collaborator);
            return ApiResponse.success("Hooray! You have successfully become a collaborator.");
        }

        collaboratorRepository.save(collaborator);
        return ApiResponse.success("Collaboration was successfully rejected.");
    }

}
