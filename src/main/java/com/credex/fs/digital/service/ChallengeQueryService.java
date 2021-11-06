package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.*; // for static metamodels
import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.repository.ChallengeRepository;
import com.credex.fs.digital.service.criteria.ChallengeCriteria;
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
 * Service for executing complex queries for {@link Challenge} entities in the database.
 * The main input is a {@link ChallengeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Challenge} or a {@link Page} of {@link Challenge} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChallengeQueryService extends QueryService<Challenge> {

    private final Logger log = LoggerFactory.getLogger(ChallengeQueryService.class);

    private final ChallengeRepository challengeRepository;

    public ChallengeQueryService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Return a {@link List} of {@link Challenge} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Challenge> findByCriteria(ChallengeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Challenge> specification = createSpecification(criteria);
        return challengeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Challenge} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Challenge> findByCriteria(ChallengeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Challenge> specification = createSpecification(criteria);
        return challengeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChallengeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Challenge> specification = createSpecification(criteria);
        return challengeRepository.count(specification);
    }

    /**
     * Function to convert {@link ChallengeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Challenge> createSpecification(ChallengeCriteria criteria) {
        Specification<Challenge> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Challenge_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Challenge_.title));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Challenge_.message));
            }
            if (criteria.getIconUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIconUrl(), Challenge_.iconUrl));
            }
            if (criteria.getRewardAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRewardAmount(), Challenge_.rewardAmount));
            }
            if (criteria.getIconId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getIconId(), root -> root.join(Challenge_.icon, JoinType.LEFT).get(Icon_.id))
                    );
            }
            if (criteria.getHashTagsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getHashTagsId(), root -> root.join(Challenge_.hashTags, JoinType.LEFT).get(HashTag_.id))
                    );
            }
            if (criteria.getUsersThatCompletedId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getUsersThatCompletedId(),
                            root -> root.join(Challenge_.usersThatCompleteds, JoinType.LEFT).get(AppUser_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
