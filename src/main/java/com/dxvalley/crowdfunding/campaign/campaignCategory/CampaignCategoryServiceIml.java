package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
//TODO: update current impl
@Service
public class CampaignCategoryServiceIml implements CampaignCategoryService {
    @Autowired
    private  CampaignCategoryRepository campaignCategoryRepository;
    @Autowired
    private CampaignSubCategoryRepository campaignSubCategoryRepository;

    @Override
    public CampaignCategory addCampaignCategory(CampaignCategory tempCampaignCategory) {
        var category = campaignCategoryRepository.findByName(tempCampaignCategory.getName());
        if(category != null){
            throw new ResourceAlreadyExistsException("There is already a category with this name!");
        }
        return campaignCategoryRepository.save(tempCampaignCategory);
    }

    @Override
    public CampaignCategory editCampaignCategory(CampaignCategory tempCampaignCategory, Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository
                .findCampaignCategoryByCampaignCategoryId(campaignCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign Category with this ID.")
                );

        campaignCategory.setName(
                tempCampaignCategory.getName() != null ?
                        tempCampaignCategory.getName() :
                        campaignCategory.getName());

        campaignCategory.setDescription(
                tempCampaignCategory.getDescription() != null ?
                        tempCampaignCategory.getDescription() :
                        campaignCategory.getDescription());

        return  campaignCategoryRepository.save(campaignCategory);
    }

    @Override
    public List<CampaignCategory> getCampaignCategories() {
        var campaignCategories = campaignCategoryRepository.findAll();
        if (campaignCategories.isEmpty()) {
            throw new ResourceNotFoundException("Currently, There is no Campaign Category.");
        }
        for (CampaignCategory campaignCategory : campaignCategories) {
            var subCategory = campaignSubCategoryRepository.
                    findCampaignSubCategoryByCampaignCategoryId(campaignCategory.getCampaignCategoryId()).get();
            campaignCategory.setCampaignSubCategories(subCategory);
        }
        return campaignCategories;
    }

    @Override
    public CampaignCategory getCampaignCategoryById(Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository
                .findCampaignCategoryByCampaignCategoryId(campaignCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign Category with this ID.")
                );

        var subCategory =  campaignSubCategoryRepository.findCampaignSubCategoryByCampaignCategoryId(campaignCategoryId).get();
        campaignCategory.setCampaignSubCategories(subCategory);
      return campaignCategory;
    }

    @Override
    public void deleteCampaignCategory(Long campaignCategoryId) {
        CampaignCategory campaignCategory = campaignCategoryRepository
                .findCampaignCategoryByCampaignCategoryId(campaignCategoryId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no campaign Category with this ID.")
                );
        campaignCategoryRepository.deleteById(campaignCategoryId);
    }
}
