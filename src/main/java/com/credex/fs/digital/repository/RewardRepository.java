package com.credex.fs.digital.repository;

import com.credex.fs.digital.domain.Reward;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Reward entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RewardRepository extends JpaRepository<Reward, Long>, JpaSpecificationExecutor<Reward> {}
