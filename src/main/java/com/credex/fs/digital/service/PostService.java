package com.credex.fs.digital.service;

import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.domain.HashTag;
import com.credex.fs.digital.domain.Post;
import com.credex.fs.digital.domain.User;
import com.credex.fs.digital.repository.PostRepository;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.web3j.utils.Strings;

/**
 * Service Implementation for managing {@link Post}.
 */
@Service
@Transactional
public class PostService {

    private final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Save a post.
     *
     * @param post the entity to save.
     * @return the persisted entity.
     */
    public Post save(Post post) {
        log.debug("Request to save Post : {}", post);
        return postRepository.save(post);
    }

    /**
     * Partially update a post.
     *
     * @param post the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Post> partialUpdate(Post post) {
        log.debug("Request to partially update Post : {}", post);

        return postRepository
            .findById(post.getId())
            .map(existingPost -> {
                if (post.getContent() != null) {
                    existingPost.setContent(post.getContent());
                }
                if (post.getImageUrl() != null) {
                    existingPost.setImageUrl(post.getImageUrl());
                }
                if (post.getImageUrlContentType() != null) {
                    existingPost.setImageUrlContentType(post.getImageUrlContentType());
                }
                if (post.getPublishedBy() != null) {
                    existingPost.setPublishedBy(post.getPublishedBy());
                }
                if (post.getNoOfLikes() != null) {
                    existingPost.setNoOfLikes(post.getNoOfLikes());
                }
                if (post.getNoOfShares() != null) {
                    existingPost.setNoOfShares(post.getNoOfShares());
                }

                return existingPost;
            })
            .map(postRepository::save);
    }

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Post> findAll(Pageable pageable) {
        log.debug("Request to get all Posts");
        return postRepository.findAll(pageable);
    }

    /**
     * Get one post by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Post> findOne(Long id) {
        log.debug("Request to get Post : {}", id);
        return postRepository.findById(id);
    }

    /**
     * Delete the post by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Post : {}", id);
        postRepository.deleteById(id);
    }

    @Transactional
    public void sharePost(Challenge challenge, User user, String imageB64) {
        String hashTags = "";
        if (!CollectionUtils.isEmpty(challenge.getHashTags())) {
            hashTags = Strings.join(challenge.getHashTags().stream().map(HashTag::getName).collect(Collectors.toList()), ",");
        }

        Post post = new Post()
            .content(
                String.format(
                    "Hey! I've just completed the %s challenge and earned %d DEED!",
                    challenge.getTitle(),
                    challenge.getRewardAmount()
                )
            )
            .imageUrl(Base64.getDecoder().decode(imageB64))
            .publishedBy(user.getFirstName())
            .createdAt(Instant.now())
            .hashTags(hashTags);

        postRepository.save(post);
    }
}
