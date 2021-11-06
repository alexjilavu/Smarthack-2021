package com.credex.fs.digital.web.rest;

import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.repository.ChallengeRepository;
import com.credex.fs.digital.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.credex.fs.digital.domain.Challenge}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ChallengeResource {

    private final Logger log = LoggerFactory.getLogger(ChallengeResource.class);

    private static final String ENTITY_NAME = "challenge";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChallengeRepository challengeRepository;

    public ChallengeResource(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * {@code POST  /challenges} : Create a new challenge.
     *
     * @param challenge the challenge to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new challenge, or with status {@code 400 (Bad Request)} if the challenge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/challenges")
    public ResponseEntity<Challenge> createChallenge(@RequestBody Challenge challenge) throws URISyntaxException {
        log.debug("REST request to save Challenge : {}", challenge);
        if (challenge.getId() != null) {
            throw new BadRequestAlertException("A new challenge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Challenge result = challengeRepository.save(challenge);
        return ResponseEntity
            .created(new URI("/api/challenges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /challenges/:id} : Updates an existing challenge.
     *
     * @param id the id of the challenge to save.
     * @param challenge the challenge to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated challenge,
     * or with status {@code 400 (Bad Request)} if the challenge is not valid,
     * or with status {@code 500 (Internal Server Error)} if the challenge couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/challenges/{id}")
    public ResponseEntity<Challenge> updateChallenge(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Challenge challenge
    ) throws URISyntaxException {
        log.debug("REST request to update Challenge : {}, {}", id, challenge);
        if (challenge.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, challenge.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!challengeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Challenge result = challengeRepository.save(challenge);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, challenge.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /challenges/:id} : Partial updates given fields of an existing challenge, field will ignore if it is null
     *
     * @param id the id of the challenge to save.
     * @param challenge the challenge to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated challenge,
     * or with status {@code 400 (Bad Request)} if the challenge is not valid,
     * or with status {@code 404 (Not Found)} if the challenge is not found,
     * or with status {@code 500 (Internal Server Error)} if the challenge couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/challenges/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Challenge> partialUpdateChallenge(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Challenge challenge
    ) throws URISyntaxException {
        log.debug("REST request to partial update Challenge partially : {}, {}", id, challenge);
        if (challenge.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, challenge.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!challengeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Challenge> result = challengeRepository
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

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, challenge.getId().toString())
        );
    }

    /**
     * {@code GET  /challenges} : get all the challenges.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of challenges in body.
     */
    @GetMapping("/challenges")
    public List<Challenge> getAllChallenges(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Challenges");
        return challengeRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /challenges/:id} : get the "id" challenge.
     *
     * @param id the id of the challenge to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the challenge, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/challenges/{id}")
    public ResponseEntity<Challenge> getChallenge(@PathVariable Long id) {
        log.debug("REST request to get Challenge : {}", id);
        Optional<Challenge> challenge = challengeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(challenge);
    }

    /**
     * {@code DELETE  /challenges/:id} : delete the "id" challenge.
     *
     * @param id the id of the challenge to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/challenges/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        log.debug("REST request to delete Challenge : {}", id);
        challengeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
