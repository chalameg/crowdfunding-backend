package com.dxvalley.crowdfunding.campaign.campaignSubCategory;

import com.dxvalley.crowdfunding.campaign.campaignCategory.CampaignCategory;
import com.dxvalley.crowdfunding.campaign.campaignCategory.CampaignCategoryRepository;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryMapper;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryReq;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryRes;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignSubCategoryServiceImpl implements CampaignSubCategoryService {
    private final CampaignSubCategoryRepository campaignSubCategoryRepository;
    private final CampaignCategoryRepository campaignCategoryRepository;

    public List<SubCategoryRes> getCampaignSubCategories() {
        List<CampaignSubCategory> campaignSubCategories = this.campaignSubCategoryRepository.findAll();
        if (campaignSubCategories.isEmpty()) {
            throw new ResourceNotFoundException("Currently, There is no Campaign SubCategory.");
        } else {
            return campaignSubCategories.stream().map(SubCategoryMapper::toSubCategoryRes).toList();
        }
    }

    public CampaignSubCategory getCampaignSubCategoryById(Short campaignSubCategoryId) {
        return (CampaignSubCategory)this.campaignSubCategoryRepository.findById(campaignSubCategoryId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no campaign subcategory with this ID.");
        });
    }

    public List<SubCategoryRes> getCampaignSubCategoryByCategory(Short campaignCategoryId) {
        List<CampaignSubCategory> campaignSubCategories = this.campaignSubCategoryRepository.findByCampaignCategoryId(campaignCategoryId);
        return campaignSubCategories.stream().map(SubCategoryMapper::toSubCategoryRes).toList();
    }

    public CampaignSubCategory addCampaignSubCategory(SubCategoryReq subCategoryReq) {
        this.validateSubCategoryNameNotExist(subCategoryReq.getName());
        CampaignCategory campaignCategory = this.getCategoryById(subCategoryReq.getCategoryId());
        CampaignSubCategory campaignSubCategory = this.createCampaignSubCategory(subCategoryReq, campaignCategory);
        return (CampaignSubCategory)this.campaignSubCategoryRepository.save(campaignSubCategory);
    }

    public CampaignSubCategory editCampaignSubCategory(Short campaignSubCategoryId, CampaignSubCategory tempcampaignSubCategory) {
        CampaignSubCategory campaignSubCategory = (CampaignSubCategory)this.campaignSubCategoryRepository.findById(campaignSubCategoryId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no campaign subcategory with this ID.");
        });
        if (tempcampaignSubCategory.getName() != null && tempcampaignSubCategory.getName().length() > 0) {
            campaignSubCategory.setName(tempcampaignSubCategory.getName());
        }

        if (tempcampaignSubCategory.getDescription() != null && tempcampaignSubCategory.getDescription().length() > 0) {
            campaignSubCategory.setDescription(tempcampaignSubCategory.getDescription());
        }

        return (CampaignSubCategory)this.campaignSubCategoryRepository.save(campaignSubCategory);
    }

    public ResponseEntity<ApiResponse> deleteCampaignSubCategory(Short campaignSubCategoryId) {
        this.getCampaignSubCategoryById(campaignSubCategoryId);
        this.campaignSubCategoryRepository.deleteById(campaignSubCategoryId);
        return ApiResponse.success("Campaign subCategory Deleted successfully!");
    }

    private void validateSubCategoryNameNotExist(String name) {
        CampaignSubCategory subCategory = this.campaignSubCategoryRepository.findByName(name);
        if (subCategory != null) {
            throw new ResourceAlreadyExistsException("There is already a sub-category with this name!");
        }
    }

    private CampaignCategory getCategoryById(Short campaignCategoryId) {
        return (CampaignCategory)this.campaignCategoryRepository.findById(campaignCategoryId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no campaign Category with this ID.");
        });
    }

    private CampaignSubCategory createCampaignSubCategory(SubCategoryReq subCategoryReq, CampaignCategory campaignCategory) {
        CampaignSubCategory campaignSubCategory = new CampaignSubCategory();
        campaignSubCategory.setName(subCategoryReq.getName());
        campaignSubCategory.setDescription(subCategoryReq.getDescription());
        campaignSubCategory.setCampaignCategory(campaignCategory);
        return campaignSubCategory;
    }

}
