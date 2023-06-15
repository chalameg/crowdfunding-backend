package com.dxvalley.crowdfunding.campaign.campaignCategory.dto;

import com.dxvalley.crowdfunding.campaign.campaignSubCategory.dto.SubCategoryRes;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {
    private Short id;
    private String name;
    private String description;
    private List<SubCategoryRes> subCategories;
}
