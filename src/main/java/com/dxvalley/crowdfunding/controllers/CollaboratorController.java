package com.dxvalley.crowdfunding.controllers;
import java.util.List;

import com.dxvalley.crowdfunding.email.EmailSender;
import com.dxvalley.crowdfunding.services.UserRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.services.CollaboratorService;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.Collaborator;
import com.dxvalley.crowdfunding.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@AllArgsConstructor
@RequestMapping("/api/collaborators")
public class CollaboratorController {
  private final CollaboratorService collaboratorService;
  private final UserRepository userRepository;
  private CampaignService campaignService;
  private EmailSender emailSender;
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
  public ResponseEntity<?> addCollaborator(@RequestBody InviteRequest inviteRequest) {

      var user = userRepository.findByUsername(inviteRequest.getInviterUsername());
      if (user == null){
          ApiResponse response = new ApiResponse(
                  "Bad Request",
                  "No user with this email");
          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
              inviteRequest.getInviterUsername(),
              registrationService.buildEmailInvitation(
                      user.getFullName(),
                      user.getFullName(),
                      campaign.getTitle(),
                      link));

      ApiResponse response = new ApiResponse("success", "Invitation sent successfully!");
      return new ResponseEntity<>(response, HttpStatus.OK);
  }

    @GetMapping("/invitationDetail/{campaignId}/{collaboratorId}")
    public String invitationDetail(@PathVariable Long campaignId,@PathVariable Long collaboratorId){
        Campaign campaign = campaignService.getCampaignById(campaignId);
        String link = "http://localhost:8181/api/collaborators/acceptOrReject/" + collaboratorId;
        return registrationService.invitationDetailPage(campaign,link);
    }

    @GetMapping("/acceptOrReject/{collaboratorId}/{flag}")
    public String acceptInvitation(@PathVariable Long collaboratorId,@PathVariable Long flag){
      if (flag==0){
          return registrationService.invitationRejected();
      }
    Collaborator collaborator = collaboratorService.getCollaboratorById(collaboratorId);
    collaborator.setEnabled(true);
    collaboratorService.editCollaborator(collaborator);
    return registrationService.invitationAccepted();
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


@Getter
@Setter
@AllArgsConstructor
class CollaboratorResponse {
  Collaborator Collaborator;
  String status;
}

@Getter
@Setter
@AllArgsConstructor
class InviteRequest {
    boolean isCampaignCreator;
    boolean isEnabled;
    String inviterUsername;
    Long campaignId;

}
