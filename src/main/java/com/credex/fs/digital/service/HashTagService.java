package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.HashTag;
import com.credex.fs.digital.repository.HashTagRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link HashTag}.
 */
@Service
@Transactional
public class HashTagService {

    private final Logger log = LoggerFactory.getLogger(HashTagService.class);

    private final HashTagRepository hashTagRepository;

    public HashTagService(HashTagRepository hashTagRepository) {
        this.hashTagRepository = hashTagRepository;
    }

    /**
     * Save a hashTag.
     *
     * @param hashTag the entity to save.
     * @return the persisted entity.
     */
    public HashTag save(HashTag hashTag) {
        log.debug("Request to save HashTag : {}", hashTag);
        return hashTagRepository.save(hashTag);
    }

    /**
     * Partially update a hashTag.
     *
     * @param hashTag the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HashTag> partialUpdate(HashTag hashTag) {
        log.debug("Request to partially update HashTag : {}", hashTag);

        return hashTagRepository
            .findById(hashTag.getId())
            .map(existingHashTag -> {
                if (hashTag.getName() != null) {
                    existingHashTag.setName(hashTag.getName());
                }
                if (hashTag.getCompany() != null) {
                    existingHashTag.setCompany(hashTag.getCompany());
                }

                return existingHashTag;
            })
            .map(hashTagRepository::save);
    }

    /**
     * Get all the hashTags.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HashTag> findAll(Pageable pageable) {
        log.debug("Request to get all HashTags");
        return hashTagRepository.findAll(pageable);
    }

    /**
     * Get one hashTag by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HashTag> findOne(Long id) {
        log.debug("Request to get HashTag : {}", id);
        return hashTagRepository.findById(id);
    }

    /**
     * Delete the hashTag by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete HashTag : {}", id);
        hashTagRepository.deleteById(id);
    }
}
