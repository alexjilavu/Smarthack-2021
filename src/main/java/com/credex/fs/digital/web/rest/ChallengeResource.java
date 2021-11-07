package com.credex.fs.digital.web.rest;

import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.repository.ChallengeRepository;
import com.credex.fs.digital.service.ChallengeQueryService;
import com.credex.fs.digital.service.ChallengeService;
import com.credex.fs.digital.service.CognitiveServicesBing;
import com.credex.fs.digital.service.ComputerVisionService;
import com.credex.fs.digital.service.criteria.ChallengeCriteria;
import com.credex.fs.digital.service.dto.ChallengeDTO;
import com.credex.fs.digital.service.dto.CompleteChallengeDTO;
import com.credex.fs.digital.service.dto.CompleteChallengeRequestDTO;
import com.credex.fs.digital.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.credex.fs.digital.domain.Challenge}.
 */
@RestController
@RequestMapping("/api")
public class ChallengeResource {

    private final Logger log = LoggerFactory.getLogger(ChallengeResource.class);

    private static final String ENTITY_NAME = "challenge";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChallengeService challengeService;

    private final ChallengeRepository challengeRepository;

    private final ChallengeQueryService challengeQueryService;

    @Autowired
    private ComputerVisionService computerVisionService;

    @Autowired
    private CognitiveServicesBing cognitiveServicesBing;

    public ChallengeResource(
        ChallengeService challengeService,
        ChallengeRepository challengeRepository,
        ChallengeQueryService challengeQueryService
    ) {
        this.challengeService = challengeService;
        this.challengeRepository = challengeRepository;
        this.challengeQueryService = challengeQueryService;
    }

    @PostMapping("/completeChallenge")
    public CompleteChallengeDTO completeChallenge(@RequestBody CompleteChallengeRequestDTO completeChallengeRequestDTO) {
        return challengeService.completeChallenge(completeChallengeRequestDTO);
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
        Challenge result = challengeService.save(challenge);
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

        Challenge result = challengeService.save(challenge);
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

        Optional<Challenge> result = challengeService.partialUpdate(challenge);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, challenge.getId().toString())
        );
    }

    /**
     * {@code GET  /challenges} : get all the challenges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of challenges in body.
     */
    @GetMapping("/challenges")
    public List<ChallengeDTO> getAllChallenges(ChallengeCriteria criteria) {
        log.debug("REST request to get Challenges by criteria: {}", criteria);

        return challengeService.getAllChallenges(criteria);
    }

    /**
     * {@code GET  /challenges/count} : count all the challenges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/challenges/count")
    public ResponseEntity<Long> countChallenges(ChallengeCriteria criteria) {
        log.debug("REST request to count Challenges by criteria: {}", criteria);
        return ResponseEntity.ok().body(challengeQueryService.countByCriteria(criteria));
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
        Optional<Challenge> challenge = challengeService.findOne(id);
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
        challengeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
    //    @GetMapping("/testImage")
    //    public String testImage() throws IOException {
    //        return cognitiveServicesBing.searchImage();
    //    }
}
