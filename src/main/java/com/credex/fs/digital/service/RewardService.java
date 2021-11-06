package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.Reward;
import com.credex.fs.digital.repository.RewardRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Reward}.
 */
@Service
@Transactional
public class RewardService {

    private final Logger log = LoggerFactory.getLogger(RewardService.class);

    private final RewardRepository rewardRepository;

    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    /**
     * Save a reward.
     *
     * @param reward the entity to save.
     * @return the persisted entity.
     */
    public Reward save(Reward reward) {
        log.debug("Request to save Reward : {}", reward);
        return rewardRepository.save(reward);
    }

    /**
     * Partially update a reward.
     *
     * @param reward the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Reward> partialUpdate(Reward reward) {
        log.debug("Request to partially update Reward : {}", reward);

        return rewardRepository
            .findById(reward.getId())
            .map(existingReward -> {
                if (reward.getValue() != null) {
                    existingReward.setValue(reward.getValue());
                }
                if (reward.getContent() != null) {
                    existingReward.setContent(reward.getContent());
                }

                return existingReward;
            })
            .map(rewardRepository::save);
    }

    /**
     * Get all the rewards.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Reward> findAll(Pageable pageable) {
        log.debug("Request to get all Rewards");
        return rewardRepository.findAll(pageable);
    }

    /**
     * Get one reward by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Reward> findOne(Long id) {
        log.debug("Request to get Reward : {}", id);
        return rewardRepository.findById(id);
    }

    /**
     * Delete the reward by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Reward : {}", id);
        rewardRepository.deleteById(id);
    }
}
