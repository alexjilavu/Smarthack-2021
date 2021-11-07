package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.AppUser;
import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.domain.User;
import com.credex.fs.digital.repository.AppUserRepository;
import com.credex.fs.digital.repository.ChallengeRepository;
import com.credex.fs.digital.security.CustomerSecurityUtils;
import com.credex.fs.digital.service.criteria.ChallengeCriteria;
import com.credex.fs.digital.service.dto.ChallengeDTO;
import com.credex.fs.digital.service.dto.CompleteChallengeDTO;
import com.credex.fs.digital.service.dto.CompleteChallengeRequestDTO;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Service Implementation for managing {@link Challenge}.
 */
@Service
@Transactional
public class ChallengeService {

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    private final ChallengeRepository challengeRepository;

    private final ComputerVisionService computerVisionService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CustomerSecurityUtils customerSecurityUtils;

    @Autowired
    private ChallengeQueryService challengeQueryService;

    public ChallengeService(ChallengeRepository challengeRepository, ComputerVisionService computerVisionService) {
        this.challengeRepository = challengeRepository;
        this.computerVisionService = computerVisionService;
    }

    /**
     * Save a challenge.
     *
     * @param challenge the entity to save.
     * @return the persisted entity.
     */
    public Challenge save(Challenge challenge) {
        log.debug("Request to save Challenge : {}", challenge);
        return challengeRepository.save(challenge);
    }

    /**
     * Partially update a challenge.
     *
     * @param challenge the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Challenge> partialUpdate(Challenge challenge) {
        log.debug("Request to partially update Challenge : {}", challenge);

        return challengeRepository
            .findById(challenge.getId())
            .map(existingChallenge -> {
                if (challenge.getTitle() != null) {
                    existingChallenge.setTitle(challenge.getTitle());
                }
                if (challenge.getMessage() != null) {
                    existingChallenge.setMessage(challenge.getMessage());
                }
                if (challenge.getIconUrl() != null) {
                    existingChallenge.setIconUrl(challenge.getIconUrl());
                }
                if (challenge.getRewardAmount() != null) {
                    existingChallenge.setRewardAmount(challenge.getRewardAmount());
                }

                return existingChallenge;
            })
            .map(challengeRepository::save);
    }

    /**
     * Get all the challenges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Challenge> findAll(Pageable pageable) {
        log.debug("Request to get all Challenges");
        return challengeRepository.findAll(pageable);
    }

    /**
     * Get all the challenges with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Challenge> findAllWithEagerRelationships(Pageable pageable) {
        return challengeRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one challenge by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Challenge> findOne(Long id) {
        log.debug("Request to get Challenge : {}", id);
        return challengeRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the challenge by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Challenge : {}", id);
        challengeRepository.deleteById(id);
    }

    @Transactional
    public CompleteChallengeDTO completeChallenge(CompleteChallengeRequestDTO completeChallengeRequestDTO) {
        Challenge challenge = challengeRepository
            .findById(completeChallengeRequestDTO.getChallengeId())
            .orElseThrow(EntityNotFoundException::new);
        User user = customerSecurityUtils.getUser().orElseThrow(EntityNotFoundException::new);

        log.info("User {} trying to complete challenge {}", user.getLogin(), challenge.getTitle());

        List<String> challengeTags = new ArrayList<>();
        if (challenge.getRequiredTags() != null) {
            challengeTags = new ArrayList<>(List.of(challenge.getRequiredTags().split(",")));
        }
        List<ImageTag> tags = computerVisionService.analyzeImage(completeChallengeRequestDTO.getB64Image());

        for (ImageTag tag : tags) {
            if (challengeTags.contains(tag.name().toLowerCase())) {
                if (tag.confidence() < 0.90d) {
                    break;
                }

                challengeTags.remove(tag.name());
            }
        }

        if (challengeTags.isEmpty()) {
            AppUser appUser = appUserRepository.findAppUserByUserId(user.getId()).orElseThrow(EntityNotFoundException::new);
            appUser.addCompletedChallenges(challenge);

            return new CompleteChallengeDTO(true);
        }

        return new CompleteChallengeDTO(false);
    }

    @Transactional
    public List<ChallengeDTO> getAllChallenges(ChallengeCriteria criteria) {
        User user = customerSecurityUtils.getUser().orElseThrow(EntityNotFoundException::new);
        AppUser appUser = appUserRepository.findAppUserByUserId(user.getId()).orElseThrow(EntityNotFoundException::new);

        return challengeQueryService
            .findByCriteria(criteria)
            .stream()
            .map(challenge -> {
                boolean completed = false;
                if (appUser.getCompletedChallenges().contains(challenge)) {
                    completed = true;
                }

                return new ChallengeDTO(challenge, completed);
            })
            .collect(Collectors.toList());
    }
}
