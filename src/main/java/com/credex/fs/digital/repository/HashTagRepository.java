package com.credex.fs.digital.repository;

import com.credex.fs.digital.domain.HashTag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HashTag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long>, JpaSpecificationExecutor<HashTag> {}
