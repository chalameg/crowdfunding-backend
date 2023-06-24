package com.dxvalley.crowdfunding.campaign.campaignCategory;

import com.dxvalley.crowdfunding.campaign.campaignCategory.dto.CategoryMapper;
import com.dxvalley.crowdfunding.campaign.campaignCategory.dto.CategoryReq;
import com.dxvalley.crowdfunding.campaign.campaignCategory.dto.CategoryResponse;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategoryService;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryRes;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignCategoryServiceIml implements CampaignCategoryService {
    private final CampaignCategoryRepository campaignCategoryRepository;
    private final CampaignSubCategoryService campaignCategoryService;

    public List<CategoryResponse> getCampaignCategories() {
        List<CampaignCategory> campaignCategories = this.campaignCategoryRepository.findAll();
        if (campaignCategories.isEmpty()) {
            throw new ResourceNotFoundException("Currently, There is no Campaign Category.");
        } else {
            List<CategoryResponse> categoryResponses = new ArrayList();
            Iterator iterator = campaignCategories.iterator();

            while(iterator.hasNext()) {
                CampaignCategory campaignCategory = (CampaignCategory)iterator.next();
                List<SubCategoryRes> subCategories = this.campaignCategoryService.getCampaignSubCategoryByCategory(campaignCategory.getId());
                CategoryResponse categoryResponse = CategoryMapper.toCategoryResponse(campaignCategory, subCategories);
                categoryResponses.add(categoryResponse);
            }

            return categoryResponses;
        }
    }

    public CategoryResponse getCampaignCategoryById(Short campaignCategoryId) {
        CampaignCategory campaignCategory = this.getCategoryById(campaignCategoryId);
        List<SubCategoryRes> subCategories = this.campaignCategoryService.getCampaignSubCategoryByCategory(campaignCategory.getId());
        CategoryResponse categoryResponse = CategoryMapper.toCategoryResponse(campaignCategory, subCategories);
        return categoryResponse;
    }

    public CampaignCategory addCampaignCategory(CategoryReq categoryReq) {
        this.validateCategoryNameNotExist(categoryReq.getName());
        CampaignCategory campaignCategory = this.createCampaignCategory(categoryReq);
        return (CampaignCategory)this.campaignCategoryRepository.save(campaignCategory);
    }

    public CampaignCategory editCampaignCategory(CampaignCategory tempCampaignCategory, Short campaignCategoryId) {
        CampaignCategory campaignCategory = (CampaignCategory)this.campaignCategoryRepository.findById(campaignCategoryId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no campaign Category with this ID.");
        });
        if (tempCampaignCategory.getName() != null) {
            campaignCategory.setName(tempCampaignCategory.getName());
        }

        if (tempCampaignCategory.getDescription() != null) {
            campaignCategory.setDescription(tempCampaignCategory.getDescription());
        }

        return (CampaignCategory)this.campaignCategoryRepository.save(campaignCategory);
    }

    public ResponseEntity<ApiResponse> deleteCampaignCategory(Short campaignCategoryId) {
        this.getCategoryById(campaignCategoryId);
        this.campaignCategoryRepository.deleteById(campaignCategoryId);
        return ApiResponse.success("CampaignCategory Deleted successfully!");
    }

    private CampaignCategory getCategoryById(Short campaignCategoryId) {
        return (CampaignCategory)this.campaignCategoryRepository.findById(campaignCategoryId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no campaign Category with this ID.");
        });
    }

    private void validateCategoryNameNotExist(String name) {
        CampaignCategory category = this.campaignCategoryRepository.findByName(name);
        if (category != null) {
            throw new ResourceAlreadyExistsException("There is already a category with this name!");
        }
    }

    private CampaignCategory createCampaignCategory(CategoryReq categoryReq) {
        CampaignCategory campaignCategory = new CampaignCategory();
        campaignCategory.setName(categoryReq.getName());
        campaignCategory.setDescription(categoryReq.getDescription());
        return campaignCategory;
    }
}
