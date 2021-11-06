package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.repository.ChallengeRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Challenge}.
 */
@Service
@Transactional
public class ChallengeService {

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
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
}
