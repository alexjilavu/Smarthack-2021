package com.credex.fs.digital.web.rest;

import com.credex.fs.digital.domain.Icon;
import com.credex.fs.digital.repository.IconRepository;
import com.credex.fs.digital.service.IconQueryService;
import com.credex.fs.digital.service.IconService;
import com.credex.fs.digital.service.criteria.IconCriteria;
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
 * REST controller for managing {@link com.credex.fs.digital.domain.Icon}.
 */
@RestController
@RequestMapping("/api")
public class IconResource {

    private final Logger log = LoggerFactory.getLogger(IconResource.class);

    private static final String ENTITY_NAME = "icon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IconService iconService;

    private final IconRepository iconRepository;

    private final IconQueryService iconQueryService;

    public IconResource(IconService iconService, IconRepository iconRepository, IconQueryService iconQueryService) {
        this.iconService = iconService;
        this.iconRepository = iconRepository;
        this.iconQueryService = iconQueryService;
    }

    /**
     * {@code POST  /icons} : Create a new icon.
     *
     * @param icon the icon to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new icon, or with status {@code 400 (Bad Request)} if the icon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/icons")
    public ResponseEntity<Icon> createIcon(@RequestBody Icon icon) throws URISyntaxException {
        log.debug("REST request to save Icon : {}", icon);
        if (icon.getId() != null) {
            throw new BadRequestAlertException("A new icon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Icon result = iconService.save(icon);
        return ResponseEntity
            .created(new URI("/api/icons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /icons/:id} : Updates an existing icon.
     *
     * @param id the id of the icon to save.
     * @param icon the icon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated icon,
     * or with status {@code 400 (Bad Request)} if the icon is not valid,
     * or with status {@code 500 (Internal Server Error)} if the icon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/icons/{id}")
    public ResponseEntity<Icon> updateIcon(@PathVariable(value = "id", required = false) final Long id, @RequestBody Icon icon)
        throws URISyntaxException {
        log.debug("REST request to update Icon : {}, {}", id, icon);
        if (icon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, icon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!iconRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Icon result = iconService.save(icon);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, icon.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /icons/:id} : Partial updates given fields of an existing icon, field will ignore if it is null
     *
     * @param id the id of the icon to save.
     * @param icon the icon to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated icon,
     * or with status {@code 400 (Bad Request)} if the icon is not valid,
     * or with status {@code 404 (Not Found)} if the icon is not found,
     * or with status {@code 500 (Internal Server Error)} if the icon couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/icons/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Icon> partialUpdateIcon(@PathVariable(value = "id", required = false) final Long id, @RequestBody Icon icon)
        throws URISyntaxException {
        log.debug("REST request to partial update Icon partially : {}, {}", id, icon);
        if (icon.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, icon.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!iconRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Icon> result = iconService.partialUpdate(icon);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, icon.getId().toString())
        );
    }

    /**
     * {@code GET  /icons} : get all the icons.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of icons in body.
     */
    @GetMapping("/icons")
    public ResponseEntity<List<Icon>> getAllIcons(IconCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Icons by criteria: {}", criteria);
        Page<Icon> page = iconQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /icons/count} : count all the icons.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/icons/count")
    public ResponseEntity<Long> countIcons(IconCriteria criteria) {
        log.debug("REST request to count Icons by criteria: {}", criteria);
        return ResponseEntity.ok().body(iconQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /icons/:id} : get the "id" icon.
     *
     * @param id the id of the icon to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the icon, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/icons/{id}")
    public ResponseEntity<Icon> getIcon(@PathVariable Long id) {
        log.debug("REST request to get Icon : {}", id);
        Optional<Icon> icon = iconService.findOne(id);
        return ResponseUtil.wrapOrNotFound(icon);
    }

    /**
     * {@code DELETE  /icons/:id} : delete the "id" icon.
     *
     * @param id the id of the icon to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/icons/{id}")
    public ResponseEntity<Void> deleteIcon(@PathVariable Long id) {
        log.debug("REST request to delete Icon : {}", id);
        iconService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
