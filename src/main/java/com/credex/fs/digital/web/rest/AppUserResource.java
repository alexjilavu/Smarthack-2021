package com.credex.fs.digital.web.rest;

import com.credex.fs.digital.domain.AppUser;
import com.credex.fs.digital.repository.AppUserRepository;
import com.credex.fs.digital.service.AppUserQueryService;
import com.credex.fs.digital.service.AppUserService;
import com.credex.fs.digital.service.criteria.AppUserCriteria;
import com.credex.fs.digital.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing {@link com.credex.fs.digital.domain.AppUser}.
 */
@RestController
@RequestMapping("/api")
public class AppUserResource {

    private final Logger log = LoggerFactory.getLogger(AppUserResource.class);

    private static final String ENTITY_NAME = "appUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppUserService appUserService;

    private final AppUserRepository appUserRepository;

    private final AppUserQueryService appUserQueryService;

    public AppUserResource(AppUserService appUserService, AppUserRepository appUserRepository, AppUserQueryService appUserQueryService) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
        this.appUserQueryService = appUserQueryService;
    }

    /**
     * {@code POST  /app-users} : Create a new appUser.
     *
     * @param appUser the appUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appUser, or with status {@code 400 (Bad Request)} if the appUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/app-users")
    public ResponseEntity<AppUser> createAppUser(@RequestBody AppUser appUser) throws URISyntaxException {
        log.debug("REST request to save AppUser : {}", appUser);
        if (appUser.getId() != null) {
            throw new BadRequestAlertException("A new appUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AppUser result = appUserService.save(appUser);
        return ResponseEntity
            .created(new URI("/api/app-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /app-users/:id} : Updates an existing appUser.
     *
     * @param id the id of the appUser to save.
     * @param appUser the appUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUser,
     * or with status {@code 400 (Bad Request)} if the appUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/app-users/{id}")
    public ResponseEntity<AppUser> updateAppUser(@PathVariable(value = "id", required = false) final Long id, @RequestBody AppUser appUser)
        throws URISyntaxException {
        log.debug("REST request to update AppUser : {}, {}", id, appUser);
        if (appUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AppUser result = appUserService.save(appUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /app-users/:id} : Partial updates given fields of an existing appUser, field will ignore if it is null
     *
     * @param id the id of the appUser to save.
     * @param appUser the appUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUser,
     * or with status {@code 400 (Bad Request)} if the appUser is not valid,
     * or with status {@code 404 (Not Found)} if the appUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the appUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/app-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AppUser> partialUpdateAppUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AppUser appUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update AppUser partially : {}, {}", id, appUser);
        if (appUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppUser> result = appUserService.partialUpdate(appUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUser.getId().toString())
        );
    }

    /**
     * {@code GET  /app-users} : get all the appUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appUsers in body.
     */
    @GetMapping("/app-users")
    public ResponseEntity<List<AppUser>> getAllAppUsers(AppUserCriteria criteria, Pageable pageable) {
        log.debug("REST request to get AppUsers by criteria: {}", criteria);
        Page<AppUser> page = appUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /app-users/count} : count all the appUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/app-users/count")
    public ResponseEntity<Long> countAppUsers(AppUserCriteria criteria) {
        log.debug("REST request to count AppUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(appUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /app-users/:id} : get the "id" appUser.
     *
     * @param id the id of the appUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/app-users/{id}")
    public ResponseEntity<AppUser> getAppUser(@PathVariable Long id) {
        log.debug("REST request to get AppUser : {}", id);
        Optional<AppUser> appUser = appUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appUser);
    }

    /**
     * {@code DELETE  /app-users/:id} : delete the "id" appUser.
     *
     * @param id the id of the appUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/app-users/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Long id) {
        log.debug("REST request to delete AppUser : {}", id);
        appUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
