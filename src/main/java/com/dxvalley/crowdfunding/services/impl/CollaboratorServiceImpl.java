package com.dxvalley.crowdfunding.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.dxvalley.crowdfunding.exceptions.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.Collaborator;
import com.dxvalley.crowdfunding.repositories.CollaboratorRepository;
import com.dxvalley.crowdfunding.services.CollaboratorService;
import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.InviteRequest;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.notification.EmailServiceImpl;
import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.services.UserService;

@Service
public class CollaboratorServiceImpl implements CollaboratorService {
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private CampaignRepository campaignRepository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Collaborator> getCollaborators() {
        var result = collaboratorRepository.findAll();
        if(result.size()==0){
            throw new ResourceNotFoundException("Currently, There is no collaborators");
        }
        return result;
    }

    @Override
    public ApiResponse sendInvitation(InviteRequest inviteRequest) {
        var invitee = userService.getUserByUsername(inviteRequest.getUsername());
        var campaign = campaignService.getCampaignById(inviteRequest.getCampaignId());
        var inviteSender =userService.getUserByUsername(campaign.getOwner());

        LocalDateTime invitationSentAt = LocalDateTime.now();
        LocalDateTime expiredAt = invitationSentAt.plusDays(1);

        Collaborator collaborator = new Collaborator();
        collaborator.setCampaign(campaign);
        collaborator.setUsers(invitee);
        collaborator.setInvitationSentAt(invitationSentAt.format(dateTimeFormatter));
        collaborator.setExpiredAt(expiredAt.format(dateTimeFormatter));
        collaborator.setIsAccepted(false);

        var result = collaboratorRepository.save(collaborator);

        String link = "http://localhost:3000/invitationDetail/" + result.getCollaboratorId();

        var isSend = emailService.send(
                inviteRequest.getUsername(),
                emailService.emailBuilderForCollaborationInvitation(
                        invitee.getFullName(),
                        inviteSender.getFullName(),
                        campaign.getTitle(),
                        link),
                "Asking for Collaboration");

        if(!isSend){
            collaboratorRepository.delete(result);
            return new ApiResponse("error", "can't send an invitation at this time. Please try again later!");
        }
        return new ApiResponse("success", "The invitation sent successfully!");
    }

    @Override
    public List<Collaborator> getCollaboratorByCampaignId(Long campaignId) {
        campaignRepository.findCampaignByCampaignId(campaignId).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );
        var  collaborator = collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
        if(collaborator.size() == 0){
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
    public ApiResponse acceptOrRejectInvitation(Long collaboratorId, Boolean flag) {

        Collaborator collaborator = getCollaboratorById(collaboratorId);
        LocalDateTime expiredAt = LocalDateTime.parse(collaborator.getExpiredAt(), dateTimeFormatter);

        if(collaborator.getRespondedAt() != null){
            throw new ResourceAlreadyExistsException("You have already responded to this invitation.");
        }

        LocalDateTime respondedAt = LocalDateTime.now();
        collaborator.setRespondedAt(respondedAt.format(dateTimeFormatter));

        if (expiredAt.isBefore(LocalDateTime.now())){
            collaboratorRepository.save(collaborator);
            return new ApiResponse("bad request","This invitation has already expired.");
        }

        if(flag){
            collaborator.setIsAccepted(true);
            collaboratorRepository.save(collaborator);
            return new ApiResponse("success","Hooray! You have successfully become a collaborator.");
        }

        collaboratorRepository.save(collaborator);
        return new ApiResponse("success","Collaboration was successfully rejected.");
    }

}
