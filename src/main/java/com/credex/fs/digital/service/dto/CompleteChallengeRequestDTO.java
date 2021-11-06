package com.credex.fs.digital.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteChallengeRequestDTO {

    private Long challengeId;

    private String b64Image;
}
