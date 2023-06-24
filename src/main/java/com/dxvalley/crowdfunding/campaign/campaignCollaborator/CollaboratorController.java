package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaborationRequest;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto.CollaboratorResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {
    private final CollaboratorService collaboratorService;

    @GetMapping({"/{id}"})
    public ResponseEntity<CollaboratorResponse> getCollaborator(@PathVariable Long id) {
        return ResponseEntity.ok(this.collaboratorService.getCollaboratorById(id));
    }

    @GetMapping({"/byCampaign/{campaignId}"})
    public ResponseEntity<List<CollaboratorResponse>> getUserCollaborators(@PathVariable Long campaignId) {
        return ResponseEntity.ok(this.collaboratorService.getCollaboratorByCampaignId(campaignId));
    }

    @PostMapping({"/invite"})
    public ResponseEntity<CollaboratorResponse> sendInvitation(@RequestBody @Valid CollaborationRequest collaborationRequest) {
        CollaboratorResponse collaboratorResponse = this.collaboratorService.sendInvitation(collaborationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(collaboratorResponse);
    }

    @PutMapping({"/respondToInvitation/{id}/{accepted}"})
    public ResponseEntity<ApiResponse> acceptInvitation(@PathVariable Long id, @PathVariable boolean accepted) {
        return this.collaboratorService.respondToCollaborationInvitation(id, accepted);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<ApiResponse> deleteCollaborator(@PathVariable Long id) {
        return this.collaboratorService.deleteCollaborator(id);
    }

}
