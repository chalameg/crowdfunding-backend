package com.dxvalley.crowdfunding.dto;

import com.dxvalley.crowdfunding.models.CampaignStage;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CampaignDTO {
    private Long campaignId;
    private String title;
    private String shortDescription;
    private String city;
    private String imageUrl;
    private Double goalAmount;
    private String projectType;
    private CampaignStage campaignStage;
    private Short campaignDuration;
    private String campaignDurationLeft;
    private String expiredAt;
    private String totalAmountCollected;
    private Integer numberOfLikes;
    private Integer numberOfBackers;

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String campaignDurationLeft(String expiredAt) {
        Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.parse(expiredAt, dateTimeFormatter));
        if (duration.toDays() == 1)
            return duration.toDays() + " day left";
        else if (duration.toDays() < 0) {
            return "This Campaign is Already Expired";
        }
        return duration.toDays() + " days left";
    }
}
