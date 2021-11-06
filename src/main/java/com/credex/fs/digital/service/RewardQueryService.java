package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.*; // for static metamodels
import com.credex.fs.digital.domain.Reward;
import com.credex.fs.digital.repository.RewardRepository;
import com.credex.fs.digital.service.criteria.RewardCriteria;
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
 * Service for executing complex queries for {@link Reward} entities in the database.
 * The main input is a {@link RewardCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Reward} or a {@link Page} of {@link Reward} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RewardQueryService extends QueryService<Reward> {

    private final Logger log = LoggerFactory.getLogger(RewardQueryService.class);

    private final RewardRepository rewardRepository;

    public RewardQueryService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    /**
     * Return a {@link List} of {@link Reward} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Reward> findByCriteria(RewardCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Reward> specification = createSpecification(criteria);
        return rewardRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Reward} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Reward> findByCriteria(RewardCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Reward> specification = createSpecification(criteria);
        return rewardRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RewardCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Reward> specification = createSpecification(criteria);
        return rewardRepository.count(specification);
    }

    /**
     * Function to convert {@link RewardCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Reward> createSpecification(RewardCriteria criteria) {
        Specification<Reward> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Reward_.id));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValue(), Reward_.value));
            }
            if (criteria.getContent() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContent(), Reward_.content));
            }
            if (criteria.getIconId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getIconId(), root -> root.join(Reward_.icon, JoinType.LEFT).get(Icon_.id))
                    );
            }
            if (criteria.getCompanyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCompanyId(), root -> root.join(Reward_.company, JoinType.LEFT).get(Company_.id))
                    );
            }
        }
        return specification;
    }
}
