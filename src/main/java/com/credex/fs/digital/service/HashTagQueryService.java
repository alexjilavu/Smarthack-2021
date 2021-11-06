package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.*; // for static metamodels
import com.credex.fs.digital.domain.HashTag;
import com.credex.fs.digital.repository.HashTagRepository;
import com.credex.fs.digital.service.criteria.HashTagCriteria;
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
 * Service for executing complex queries for {@link HashTag} entities in the database.
 * The main input is a {@link HashTagCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link HashTag} or a {@link Page} of {@link HashTag} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HashTagQueryService extends QueryService<HashTag> {

    private final Logger log = LoggerFactory.getLogger(HashTagQueryService.class);

    private final HashTagRepository hashTagRepository;

    public HashTagQueryService(HashTagRepository hashTagRepository) {
        this.hashTagRepository = hashTagRepository;
    }

    /**
     * Return a {@link List} of {@link HashTag} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<HashTag> findByCriteria(HashTagCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<HashTag> specification = createSpecification(criteria);
        return hashTagRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link HashTag} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<HashTag> findByCriteria(HashTagCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<HashTag> specification = createSpecification(criteria);
        return hashTagRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HashTagCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<HashTag> specification = createSpecification(criteria);
        return hashTagRepository.count(specification);
    }

    /**
     * Function to convert {@link HashTagCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<HashTag> createSpecification(HashTagCriteria criteria) {
        Specification<HashTag> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), HashTag_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), HashTag_.name));
            }
            if (criteria.getCompany() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCompany(), HashTag_.company));
            }
            if (criteria.getChallengesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getChallengesId(),
                            root -> root.join(HashTag_.challenges, JoinType.LEFT).get(Challenge_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
