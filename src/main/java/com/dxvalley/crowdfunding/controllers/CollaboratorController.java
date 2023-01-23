package com.dxvalley.crowdfunding.controllers;
import java.util.List;
import java.util.stream.Collectors;

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
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api/collaborators")
public class CollaboratorController {
  private final CollaboratorService collaboratorService;
  private final UserRepository userRepository;
  private CampaignService campaignService;
  
  
  public CollaboratorController(CollaboratorService collaboratorService, UserRepository userRepository, CampaignService campaignService) {
    this.collaboratorService = collaboratorService;
    this.userRepository = userRepository;
    this.campaignService = campaignService;
  }

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
      
      Users user = userRepository.findByUsername(inviteRequest.getInviterUsername());
      Campaign campaign = campaignService.getCampaignById(inviteRequest.getCampaignId());
      Collaborator collaborator = new Collaborator();
      collaborator.setCampaign(campaign);
      collaborator.setUsers(user);
      collaborator.setCampaignCreator(false);
      collaborator.setEnabled(false);
      collaboratorService.addCollaborator(collaborator);

      //send collaborator a link to accept collaboration using email/phone

      ApiResponse response = new ApiResponse("success", "Invitation sent successfully!");

      return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/accept/{collaboratorId}")
  public ResponseEntity<?> accept(@PathVariable Long collaboratorId){
   
    Collaborator collaborator = collaboratorService.getCollaborators().stream().filter(c -> c.getCollaboratorId() == collaboratorId).collect(Collectors.toList()).get(0);
    collaborator.setEnabled(true);

    return new ResponseEntity<>(collaborator, HttpStatus.OK);
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
