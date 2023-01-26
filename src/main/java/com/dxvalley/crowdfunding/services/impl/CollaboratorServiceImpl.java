package com.dxvalley.crowdfunding.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.Collaborator;
import com.dxvalley.crowdfunding.repositories.CollaboratorRepository;
import com.dxvalley.crowdfunding.services.CollaboratorService;

@Service
public class CollaboratorServiceImpl implements CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;

    public CollaboratorServiceImpl(CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }
    
    @Override
    public Collaborator addCollaborator(Collaborator collaborator) {
        return this.collaboratorRepository.save(collaborator);
    }

    @Override
    public Collaborator editCollaborator(Collaborator collaborator) {
        return this.collaboratorRepository.save(collaborator);
    }

    @Override
    public List<Collaborator> getCollaborators() {
        return this.collaboratorRepository.findAll();
    }

    @Override
    public Collaborator getCollaboratorById(Long CollaboratorId) {
        return this.collaboratorRepository.findCollaboratorByCollaboratorId(CollaboratorId);
    }

    @Override
    public void deleteCollaborator(Long CollaboratorId) {
        collaboratorRepository.deleteById(CollaboratorId);
    }

    @Override
    public List<Collaborator> findAllCollaboratorByCampaignCampaignId(Long campaignId) {
        return collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
    }
    
    
}
