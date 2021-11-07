package com.credex.fs.digital.service.dto;

import com.credex.fs.digital.domain.Company;
import com.credex.fs.digital.domain.Icon;
import com.credex.fs.digital.domain.Reward;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardDTO {

    private Long id;

    private Long value;

    private String content;

    private Icon icon;

    private Company company;

    private boolean completed;

    private String code;

    public RewardDTO(Reward reward, boolean completed, String userLogin, Long appUserId) {
        this.id = reward.getId();
        this.value = reward.getValue();
        this.content = reward.getContent();
        this.icon = reward.getIcon();
        this.company = reward.getCompany();
        this.completed = completed;

        if (completed) {
            String identifier = String.format("%s_%d_%d", userLogin, appUserId, reward.getId());
            this.code = DigestUtils.sha256Hex(identifier).substring(0, 8);
        }
    }
}
