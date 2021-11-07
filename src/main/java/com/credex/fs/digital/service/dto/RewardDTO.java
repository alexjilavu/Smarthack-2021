package com.credex.fs.digital.service.dto;

import com.credex.fs.digital.domain.Company;
import com.credex.fs.digital.domain.Icon;
import com.credex.fs.digital.domain.Reward;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public RewardDTO(Reward reward, boolean completed) {
        this.id = reward.getId();
        this.value = reward.getValue();
        this.content = reward.getContent();
        this.icon = reward.getIcon();
        this.company = reward.getCompany();
        this.completed = completed;
    }
}
