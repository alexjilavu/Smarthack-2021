package com.credex.fs.digital.repository;

import com.credex.fs.digital.domain.Challenge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Challenge entity.
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query(
        value = "select distinct challenge from Challenge challenge left join fetch challenge.hashTags",
        countQuery = "select count(distinct challenge) from Challenge challenge"
    )
    Page<Challenge> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct challenge from Challenge challenge left join fetch challenge.hashTags")
    List<Challenge> findAllWithEagerRelationships();

    @Query("select challenge from Challenge challenge left join fetch challenge.hashTags where challenge.id =:id")
    Optional<Challenge> findOneWithEagerRelationships(@Param("id") Long id);
}
