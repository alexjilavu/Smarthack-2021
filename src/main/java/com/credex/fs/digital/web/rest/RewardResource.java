package com.credex.fs.digital.web.rest;

import com.credex.fs.digital.domain.AppUser;
import com.credex.fs.digital.domain.Reward;
import com.credex.fs.digital.domain.User;
import com.credex.fs.digital.repository.AppUserRepository;
import com.credex.fs.digital.repository.RewardRepository;
import com.credex.fs.digital.security.CustomerSecurityUtils;
import com.credex.fs.digital.service.RewardQueryService;
import com.credex.fs.digital.service.RewardService;
import com.credex.fs.digital.service.criteria.RewardCriteria;
import com.credex.fs.digital.service.dto.ChallengeDTO;
import com.credex.fs.digital.service.dto.RedeemRewardDTO;
import com.credex.fs.digital.service.dto.RewardDTO;
import com.credex.fs.digital.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.credex.fs.digital.domain.Reward}.
 */
@RestController
@RequestMapping("/api")
public class RewardResource {

    private final Logger log = LoggerFactory.getLogger(RewardResource.class);

    private static final String ENTITY_NAME = "reward";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RewardService rewardService;

    private final RewardRepository rewardRepository;

    private final RewardQueryService rewardQueryService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CustomerSecurityUtils customerSecurityUtils;

    public RewardResource(RewardService rewardService, RewardRepository rewardRepository, RewardQueryService rewardQueryService) {
        this.rewardService = rewardService;
        this.rewardRepository = rewardRepository;
        this.rewardQueryService = rewardQueryService;
    }

    /**
     * {@code POST  /rewards} : Create a new reward.
     *
     * @param reward the reward to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reward, or with status {@code 400 (Bad Request)} if the reward has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rewards")
    public ResponseEntity<Reward> createReward(@RequestBody Reward reward) throws URISyntaxException {
        log.debug("REST request to save Reward : {}", reward);
        if (reward.getId() != null) {
            throw new BadRequestAlertException("A new reward cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Reward result = rewardService.save(reward);
        return ResponseEntity
            .created(new URI("/api/rewards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rewards/:id} : Updates an existing reward.
     *
     * @param id the id of the reward to save.
     * @param reward the reward to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reward,
     * or with status {@code 400 (Bad Request)} if the reward is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reward couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rewards/{id}")
    public ResponseEntity<Reward> updateReward(@PathVariable(value = "id", required = false) final Long id, @RequestBody Reward reward)
        throws URISyntaxException {
        log.debug("REST request to update Reward : {}, {}", id, reward);
        if (reward.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reward.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rewardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Reward result = rewardService.save(reward);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reward.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /rewards/:id} : Partial updates given fields of an existing reward, field will ignore if it is null
     *
     * @param id the id of the reward to save.
     * @param reward the reward to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reward,
     * or with status {@code 400 (Bad Request)} if the reward is not valid,
     * or with status {@code 404 (Not Found)} if the reward is not found,
     * or with status {@code 500 (Internal Server Error)} if the reward couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rewards/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Reward> partialUpdateReward(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Reward reward
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reward partially : {}, {}", id, reward);
        if (reward.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reward.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rewardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Reward> result = rewardService.partialUpdate(reward);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reward.getId().toString())
        );
    }

    /**
     * {@code GET  /rewards} : get all the rewards.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rewards in body.
     */
    @GetMapping("/rewards")
    @Transactional
    public List<RewardDTO> getAllRewards(RewardCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Rewards by criteria: {}", criteria);

        User user = customerSecurityUtils.getUser().orElseThrow(EntityNotFoundException::new);
        AppUser appUser = appUserRepository.findAppUserByUserId(user.getId()).orElseThrow(EntityNotFoundException::new);

        return rewardQueryService
            .findByCriteria(criteria)
            .stream()
            .map(reward -> {
                boolean completed = false;
                if (appUser.getCompletedRewards().contains(reward)) {
                    completed = true;
                }

                return new RewardDTO(reward, completed, user.getLogin(), appUser.getId().toString());
            })
            .collect(Collectors.toList());
    }

    /**
     * {@code GET  /rewards/count} : count all the rewards.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/rewards/count")
    public ResponseEntity<Long> countRewards(RewardCriteria criteria) {
        log.debug("REST request to count Rewards by criteria: {}", criteria);
        return ResponseEntity.ok().body(rewardQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /rewards/:id} : get the "id" reward.
     *
     * @param id the id of the reward to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reward, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rewards/{id}")
    public ResponseEntity<Reward> getReward(@PathVariable Long id) {
        log.debug("REST request to get Reward : {}", id);
        Optional<Reward> reward = rewardService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reward);
    }

    /**
     * {@code DELETE  /rewards/:id} : delete the "id" reward.
     *
     * @param id the id of the reward to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rewards/{id}")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        log.debug("REST request to delete Reward : {}", id);
        rewardService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/redeemReward")
    public String redeemReward(@RequestBody @Valid RedeemRewardDTO rewardDTO) {
        return rewardService.redeemReward(rewardDTO.getRewardId());
    }
}
