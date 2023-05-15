package com.dxvalley.crowdfunding.campaign.campaignSharing;

import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignShareResponse;
import com.dxvalley.crowdfunding.campaign.campaignSharing.dto.CampaignSharingDTO;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignSharingServiceImpl implements CampaignSharingService {
    private final CampaignSharingRepository campaignSharingRepository;
    private final DateTimeFormatter dateTimeFormatter;

    @Override
    /**
     * Retrieves the share counts for a campaign based on the campaign ID.
     *
     * @param campaignId the ID of the campaign
     * @return the CampaignShareResponse object containing the share counts for different platforms
     */
    public CampaignShareResponse getByCampaignId(Long campaignId) {
        try {
            List<CampaignSharing> campaignShares = campaignSharingRepository.findByCampaignId(campaignId);
            CampaignShareResponse campaignShareResponse = new CampaignShareResponse();

            campaignShares.stream()
                    .map(CampaignSharing::getSharingPlatform)
                    .map(SharingPlatform::valueOf)
                    .forEach(sharingPlatform -> {
                        int shareCount = calculateShareCount(campaignShares, sharingPlatform);
                        setShareCount(campaignShareResponse, sharingPlatform, shareCount);
                    });

            return campaignShareResponse;
        } catch (DataAccessException ex) {
            log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                    getClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    ex.getMessage());

            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    /**
     * Calculates the total share count for a specific sharing platform.
     *
     * @param campaignShares  the list of campaign shares
     * @param sharingPlatform the sharing platform to calculate the share count for
     * @return the total share count for the specified sharing platform
     */
    private int calculateShareCount(List<CampaignSharing> campaignShares, SharingPlatform sharingPlatform) {
        return campaignShares.stream()
                .filter(campaignSharing -> campaignSharing.getSharingPlatform().equals(sharingPlatform.name()))
                .mapToInt(CampaignSharing::getShareCount)
                .sum();
    }

    /**
     * Sets the share count for a specific sharing platform in the CampaignShareResponse object.
     *
     * @param campaignShareResponse the CampaignShareResponse object to set the share count on
     * @param sharingPlatform       the sharing platform to set the share count for
     * @param shareCount            the share count to set
     */
    private void setShareCount(CampaignShareResponse campaignShareResponse, SharingPlatform sharingPlatform, int shareCount) {
        switch (sharingPlatform) {
            case FACEBOOK:
                campaignShareResponse.setFacebookShares(shareCount);
                break;
            case TIKTOK:
                campaignShareResponse.setTiktokShares(shareCount);
                break;
            case TWITTER:
                campaignShareResponse.setTwitterShares(shareCount);
                break;
            case TELEGRAM:
                campaignShareResponse.setTelegramShares(shareCount);
                break;
            case WHATSAPP:
                campaignShareResponse.setWhatsappShares(shareCount);
                break;
            case LINKEDIN:
                campaignShareResponse.setLinkedinShares(shareCount);
                break;
            case INSTAGRAM:
                campaignShareResponse.setInstagramShares(shareCount);
                break;
            default:
                campaignShareResponse.setOtherShares(shareCount);
                break;
        }
    }

    /**
     * Adds a share for a campaign.
     *
     * @param campaignSharingDTO The DTO containing the information for the campaign share.
     * @return The CampaignSharing object representing the updated or newly created share.
     */

    @Override
    @Transactional
    public CampaignSharing addShareCampaign(CampaignSharingDTO campaignSharingDTO) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            CampaignSharing existingSharing = findExistingSharing(campaignSharingDTO, username);
            if (existingSharing != null) {
                existingSharing.setShareCount(existingSharing.getShareCount() + 1);
                existingSharing.setSharingTime(LocalDateTime.now().format(dateTimeFormatter));
                return campaignSharingRepository.save(existingSharing);
            }

            CampaignSharing newSharing = createNewSharing(campaignSharingDTO, username);
            return campaignSharingRepository.save(newSharing);
        } catch (DataAccessException ex) {
            log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                    getClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    ex.getMessage());

            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    /**
     * Finds an existing campaign share based on the campaign ID, username, and sharing platform.
     *
     * @param campaignSharingDTO The DTO containing the information for the campaign share.
     * @return The existing CampaignSharing object, or null if not found.
     */
    private CampaignSharing findExistingSharing(CampaignSharingDTO campaignSharingDTO, String username) {
        if (!username.equals("anonymousUser")) {
            return campaignSharingRepository.findByCampaignIdAndUsernameAndSharingPlatform(
                    campaignSharingDTO.getCampaignId(), username, SharingPlatform.lookup(campaignSharingDTO.getSharingPlatform()));
        }
        return null;
    }

    private CampaignSharing createNewSharing(CampaignSharingDTO campaignSharingDTO, String username) {
        CampaignSharing newSharing = new CampaignSharing();
        newSharing.setCampaignId(campaignSharingDTO.getCampaignId());
        newSharing.setUsername(username);
        newSharing.setSharingPlatform(SharingPlatform.lookup(campaignSharingDTO.getSharingPlatform()));
        newSharing.setSharingTime(LocalDateTime.now().format(dateTimeFormatter));
        newSharing.setShareCount(1);
        return newSharing;
    }
}
