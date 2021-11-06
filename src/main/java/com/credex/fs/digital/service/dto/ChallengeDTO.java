package com.credex.fs.digital.service.dto;

import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.domain.HashTag;
import com.credex.fs.digital.domain.Icon;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDTO {

    private Long id;

    private String title;

    private String message;

    private String iconUrl;

    private Long rewardAmount;

    private String requiredTags;

    private Icon icon;

    private boolean completed;

    private Set<HashTag> hashTags;

    public ChallengeDTO(Challenge challenge, boolean completed) {
        this.id = challenge.getId();
        this.title = challenge.getTitle();
        this.message = challenge.getMessage();
        this.iconUrl = challenge.getIconUrl();
        this.rewardAmount = challenge.getRewardAmount();
        this.requiredTags = challenge.getRequiredTags();
        this.icon = challenge.getIcon();
        this.completed = completed;
        this.hashTags = challenge.getHashTags();
    }
}
