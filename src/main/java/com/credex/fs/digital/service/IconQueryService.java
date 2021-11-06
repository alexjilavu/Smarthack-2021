package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.*; // for static metamodels
import com.credex.fs.digital.domain.Icon;
import com.credex.fs.digital.repository.IconRepository;
import com.credex.fs.digital.service.criteria.IconCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Icon} entities in the database.
 * The main input is a {@link IconCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Icon} or a {@link Page} of {@link Icon} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IconQueryService extends QueryService<Icon> {

    private final Logger log = LoggerFactory.getLogger(IconQueryService.class);

    private final IconRepository iconRepository;

    public IconQueryService(IconRepository iconRepository) {
        this.iconRepository = iconRepository;
    }

    /**
     * Return a {@link List} of {@link Icon} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Icon> findByCriteria(IconCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Icon> specification = createSpecification(criteria);
        return iconRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Icon} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Icon> findByCriteria(IconCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Icon> specification = createSpecification(criteria);
        return iconRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IconCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Icon> specification = createSpecification(criteria);
        return iconRepository.count(specification);
    }

    /**
     * Function to convert {@link IconCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Icon> createSpecification(IconCriteria criteria) {
        Specification<Icon> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Icon_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Icon_.name));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Icon_.url));
            }
        }
        return specification;
    }
}
