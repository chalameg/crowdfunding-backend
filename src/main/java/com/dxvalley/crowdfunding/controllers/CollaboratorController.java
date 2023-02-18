package com.dxvalley.crowdfunding.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dxvalley.crowdfunding.services.CollaboratorService;
import com.dxvalley.crowdfunding.dto.InviteRequest;


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
        var response = collaboratorService.sendInvitation(inviteRequest);
        if(response.getStatus() == "error")
          return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/acceptOrReject/{collaboratorId}/{flag}")
  public ResponseEntity<?> acceptInvitation(@PathVariable Long collaboratorId,@PathVariable Boolean flag){
    var response = collaboratorService.acceptOrRejectInvitation(collaboratorId, flag);
    if(response.getStatus()=="success") return new ResponseEntity<>(
            response,
            HttpStatus.OK);
  return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @DeleteMapping("/{collaboratorId}")
  public ResponseEntity<?> deleteCollaborator(@PathVariable Long collaboratorId) {
    return new ResponseEntity<>(
            collaboratorService.deleteCollaborator(collaboratorId),
            HttpStatus.OK);
  }
}
