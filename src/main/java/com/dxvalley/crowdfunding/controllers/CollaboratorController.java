package com.dxvalley.crowdfunding.controllers;
import java.util.List;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.InviteRequest;
import com.dxvalley.crowdfunding.email.EmailSender;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.services.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.services.CollaboratorService;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.Collaborator;
import com.dxvalley.crowdfunding.repositories.UserRepository;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/collaborators")
public class CollaboratorController {
  private final CollaboratorService collaboratorService;
  private final UserRepository userRepository;
  private final CampaignService campaignService;
  private final EmailSender emailSender;
  private final UserRegistrationService registrationService;


  @GetMapping
  List<Collaborator> getCollaborators() {
      return this.collaboratorService.getCollaborators();
  }

  @GetMapping("/{collaboratorId}")
  Collaborator getCollaborator(@PathVariable Long collaboratorId) {
        Collaborator collaborator = collaboratorService.getCollaboratorById(collaboratorId);
        return collaborator ;
  }

  @GetMapping("/getCollaborators/{campaignId}")
  List<Collaborator> getUserCollaborators(@PathVariable Long campaignId) {
        List<Collaborator> collaborator = collaboratorService.findAllCollaboratorByCampaignCampaignId(campaignId);
        return collaborator ;
  }

  @PostMapping("/invite")
  public ResponseEntity<?> addCollaborator(@RequestBody @Valid InviteRequest inviteRequest)
          throws ResourceNotFoundException {

      var user = userRepository.findByUsername(inviteRequest.getUsername());
      if (user == null){
          throw  new ResourceNotFoundException("There is no user with this username.");
      }
      Campaign campaign = campaignService.getCampaignById(inviteRequest.getCampaignId());
      Collaborator collaborator = new Collaborator();
      collaborator.setCampaign(campaign);
      collaborator.setUsers(user);
      collaborator.setCampaignCreator(false);
      collaborator.setEnabled(false);
      var res =  collaboratorService.addCollaborator(collaborator);
      String link = "http://localhost:8181/api/collaborators/invitationDetail/"
              + campaign.getCampaignId() + "/" + res.getCollaboratorId();

      System.out.println(link);

      emailSender.send(
              inviteRequest.getUsername(),
              emailSender.buildEmailInvitation(
                      user.getFullName(),
                      user.getFullName(),
                      campaign.getTitle(),
                      link));

      ApiResponse response = new ApiResponse("success", "Invitation sent successfully!");
      return new ResponseEntity<>(response, HttpStatus.OK);
  }

    @GetMapping("/invitationDetail/{campaignId}/{collaboratorId}")
    public String invitationDetail(@PathVariable Long campaignId,@PathVariable Long collaboratorId) throws ResourceNotFoundException {
        Campaign campaign = campaignService.getCampaignById(campaignId);
        String link = "http://localhost:8181/api/collaborators/acceptOrReject/" + collaboratorId;
        return emailSender.invitationDetailPage(campaign,link);
    }

    @GetMapping("/acceptOrReject/{collaboratorId}/{flag}")
    public String acceptInvitation(@PathVariable Long collaboratorId,@PathVariable Long flag){
      if (flag==0){
          return emailSender.invitationRejected();
      }
    Collaborator collaborator = collaboratorService.getCollaboratorById(collaboratorId);
    collaborator.setEnabled(true);
    collaboratorService.editCollaborator(collaborator);
    return emailSender.invitationAccepted();
    }


  @PutMapping("/{collaboratorId}")
  Collaborator editCollaborator(@RequestBody Collaborator tempCollaborator, @PathVariable Long collaboratorId) {
    Collaborator collaborator = this.collaboratorService.getCollaboratorById(collaboratorId);
    collaborator.setCollaboratorId(tempCollaborator.getCollaboratorId() != null ? tempCollaborator.getCollaboratorId() : collaborator.getCollaboratorId());
    return collaboratorService.editCollaborator(collaborator);
  }

  @DeleteMapping("/{collaboratorId}")
  ResponseEntity<?> deleteCollaborator(@PathVariable Long collaboratorId) {
    Collaborator collaborator = this.collaboratorService.getCollaboratorById(collaboratorId);

    if(collaborator == null) return new ResponseEntity<String>("Entry does not exist!", HttpStatus.BAD_REQUEST);

    collaboratorService.deleteCollaborator(collaboratorId);

    ApiResponse response = new ApiResponse("success", "Collaborator Deleted successfully!");

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
