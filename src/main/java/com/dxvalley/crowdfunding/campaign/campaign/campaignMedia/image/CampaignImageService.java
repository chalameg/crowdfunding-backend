package com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image;

import com.dxvalley.crowdfunding.fileUploadManager.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class CampaignImageService {
    private final CampaignImageRepository campaignImageRepository;
    private final FileUploadService fileUploadService;

    public CampaignImage saveCampaignImage(MultipartFile multipartFile) {
        String imageUrl = fileUploadService.uploadFile(multipartFile);
        
        CampaignImage campaignImage = CampaignImage.builder()
                .imageUrl(imageUrl)
                .isPrimary(true)
                .build();

        return campaignImageRepository.save(campaignImage);
    }

    public void deleteCampaignImage(Long imageId) {
        campaignImageRepository.deleteById(imageId);
    }

}

