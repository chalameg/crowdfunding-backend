package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.user.dto.InviteRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/collaborators")
public class CollaboratorController {
  @Autowired
  private CollaboratorService collaboratorService;

  @GetMapping
  public ResponseEntity<?> getCollaborators() {
      return new ResponseEntity<>(
              collaboratorService.getCollaborators(),
              HttpStatus.OK);
  }

  @GetMapping("getCollaboratorById/{collaboratorId}")
  public ResponseEntity<?> getCollaborator(@PathVariable Long collaboratorId) {
        return new ResponseEntity<>(
                collaboratorService.getCollaboratorById(collaboratorId),
                HttpStatus.OK);
  }

  @GetMapping("/getCollaboratorByCampaign/{campaignId}")
  public ResponseEntity<?> getUserCollaborators(@PathVariable Long campaignId) {
       return new ResponseEntity<>(
               collaboratorService.getCollaboratorByCampaignId(campaignId),
               HttpStatus.OK);
  }


  @PostMapping("/invite")
  public ResponseEntity<?> addCollaborator(@RequestBody @Valid InviteRequest inviteRequest) {
      return collaboratorService.sendInvitation(inviteRequest);
  }

  @PutMapping("/acceptOrReject/{collaboratorId}/{flag}")
  public ResponseEntity<?> acceptInvitation(@PathVariable Long collaboratorId,@PathVariable Boolean flag){
      return collaboratorService.acceptOrRejectInvitation(collaboratorId, flag);
  }

  @DeleteMapping("/{collaboratorId}")
  public ResponseEntity<?> deleteCollaborator(@PathVariable Long collaboratorId) {
    return new ResponseEntity<>(
            collaboratorService.deleteCollaborator(collaboratorId),
            HttpStatus.OK);
  }
}
