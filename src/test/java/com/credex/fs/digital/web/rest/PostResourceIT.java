package com.credex.fs.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.credex.fs.digital.IntegrationTest;
import com.credex.fs.digital.domain.Post;
import com.credex.fs.digital.repository.PostRepository;
import com.credex.fs.digital.service.criteria.PostCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PostResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE_URL = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_URL = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_URL_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_URL_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_PUBLISHED_BY = "AAAAAAAAAA";
    private static final String UPDATED_PUBLISHED_BY = "BBBBBBBBBB";

    private static final Integer DEFAULT_NO_OF_LIKES = 1;
    private static final Integer UPDATED_NO_OF_LIKES = 2;
    private static final Integer SMALLER_NO_OF_LIKES = 1 - 1;

    private static final Integer DEFAULT_NO_OF_SHARES = 1;
    private static final Integer UPDATED_NO_OF_SHARES = 2;
    private static final Integer SMALLER_NO_OF_SHARES = 1 - 1;

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostMockMvc;

    private Post post;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
            .content(DEFAULT_CONTENT)
            .imageUrl(DEFAULT_IMAGE_URL)
            .imageUrlContentType(DEFAULT_IMAGE_URL_CONTENT_TYPE)
            .publishedBy(DEFAULT_PUBLISHED_BY)
            .noOfLikes(DEFAULT_NO_OF_LIKES)
            .noOfShares(DEFAULT_NO_OF_SHARES);
        return post;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity(EntityManager em) {
        Post post = new Post()
            .content(UPDATED_CONTENT)
            .imageUrl(UPDATED_IMAGE_URL)
            .imageUrlContentType(UPDATED_IMAGE_URL_CONTENT_TYPE)
            .publishedBy(UPDATED_PUBLISHED_BY)
            .noOfLikes(UPDATED_NO_OF_LIKES)
            .noOfShares(UPDATED_NO_OF_SHARES);
        return post;
    }

    @BeforeEach
    public void initTest() {
        post = createEntity(em);
    }

    @Test
    @Transactional
    void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();
        // Create the Post
        restPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testPost.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testPost.getImageUrlContentType()).isEqualTo(DEFAULT_IMAGE_URL_CONTENT_TYPE);
        assertThat(testPost.getPublishedBy()).isEqualTo(DEFAULT_PUBLISHED_BY);
        assertThat(testPost.getNoOfLikes()).isEqualTo(DEFAULT_NO_OF_LIKES);
        assertThat(testPost.getNoOfShares()).isEqualTo(DEFAULT_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void createPostWithExistingId() throws Exception {
        // Create the Post with an existing ID
        post.setId(1L);

        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].imageUrlContentType").value(hasItem(DEFAULT_IMAGE_URL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_URL))))
            .andExpect(jsonPath("$.[*].publishedBy").value(hasItem(DEFAULT_PUBLISHED_BY)))
            .andExpect(jsonPath("$.[*].noOfLikes").value(hasItem(DEFAULT_NO_OF_LIKES)))
            .andExpect(jsonPath("$.[*].noOfShares").value(hasItem(DEFAULT_NO_OF_SHARES)));
    }

    @Test
    @Transactional
    void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc
            .perform(get(ENTITY_API_URL_ID, post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.imageUrlContentType").value(DEFAULT_IMAGE_URL_CONTENT_TYPE))
            .andExpect(jsonPath("$.imageUrl").value(Base64Utils.encodeToString(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.publishedBy").value(DEFAULT_PUBLISHED_BY))
            .andExpect(jsonPath("$.noOfLikes").value(DEFAULT_NO_OF_LIKES))
            .andExpect(jsonPath("$.noOfShares").value(DEFAULT_NO_OF_SHARES));
    }

    @Test
    @Transactional
    void getPostsByIdFiltering() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        Long id = post.getId();

        defaultPostShouldBeFound("id.equals=" + id);
        defaultPostShouldNotBeFound("id.notEquals=" + id);

        defaultPostShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.greaterThan=" + id);

        defaultPostShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content equals to DEFAULT_CONTENT
        defaultPostShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the postList where content equals to UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllPostsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content not equals to DEFAULT_CONTENT
        defaultPostShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the postList where content not equals to UPDATED_CONTENT
        defaultPostShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllPostsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultPostShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the postList where content equals to UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllPostsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content is not null
        defaultPostShouldBeFound("content.specified=true");

        // Get all the postList where content is null
        defaultPostShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByContentContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content contains DEFAULT_CONTENT
        defaultPostShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the postList where content contains UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllPostsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content does not contain DEFAULT_CONTENT
        defaultPostShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the postList where content does not contain UPDATED_CONTENT
        defaultPostShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllPostsByPublishedByIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publishedBy equals to DEFAULT_PUBLISHED_BY
        defaultPostShouldBeFound("publishedBy.equals=" + DEFAULT_PUBLISHED_BY);

        // Get all the postList where publishedBy equals to UPDATED_PUBLISHED_BY
        defaultPostShouldNotBeFound("publishedBy.equals=" + UPDATED_PUBLISHED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByPublishedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publishedBy not equals to DEFAULT_PUBLISHED_BY
        defaultPostShouldNotBeFound("publishedBy.notEquals=" + DEFAULT_PUBLISHED_BY);

        // Get all the postList where publishedBy not equals to UPDATED_PUBLISHED_BY
        defaultPostShouldBeFound("publishedBy.notEquals=" + UPDATED_PUBLISHED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByPublishedByIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publishedBy in DEFAULT_PUBLISHED_BY or UPDATED_PUBLISHED_BY
        defaultPostShouldBeFound("publishedBy.in=" + DEFAULT_PUBLISHED_BY + "," + UPDATED_PUBLISHED_BY);

        // Get all the postList where publishedBy equals to UPDATED_PUBLISHED_BY
        defaultPostShouldNotBeFound("publishedBy.in=" + UPDATED_PUBLISHED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByPublishedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publishedBy is not null
        defaultPostShouldBeFound("publishedBy.specified=true");

        // Get all the postList where publishedBy is null
        defaultPostShouldNotBeFound("publishedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByPublishedByContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publishedBy contains DEFAULT_PUBLISHED_BY
        defaultPostShouldBeFound("publishedBy.contains=" + DEFAULT_PUBLISHED_BY);

        // Get all the postList where publishedBy contains UPDATED_PUBLISHED_BY
        defaultPostShouldNotBeFound("publishedBy.contains=" + UPDATED_PUBLISHED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByPublishedByNotContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publishedBy does not contain DEFAULT_PUBLISHED_BY
        defaultPostShouldNotBeFound("publishedBy.doesNotContain=" + DEFAULT_PUBLISHED_BY);

        // Get all the postList where publishedBy does not contain UPDATED_PUBLISHED_BY
        defaultPostShouldBeFound("publishedBy.doesNotContain=" + UPDATED_PUBLISHED_BY);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes equals to DEFAULT_NO_OF_LIKES
        defaultPostShouldBeFound("noOfLikes.equals=" + DEFAULT_NO_OF_LIKES);

        // Get all the postList where noOfLikes equals to UPDATED_NO_OF_LIKES
        defaultPostShouldNotBeFound("noOfLikes.equals=" + UPDATED_NO_OF_LIKES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes not equals to DEFAULT_NO_OF_LIKES
        defaultPostShouldNotBeFound("noOfLikes.notEquals=" + DEFAULT_NO_OF_LIKES);

        // Get all the postList where noOfLikes not equals to UPDATED_NO_OF_LIKES
        defaultPostShouldBeFound("noOfLikes.notEquals=" + UPDATED_NO_OF_LIKES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes in DEFAULT_NO_OF_LIKES or UPDATED_NO_OF_LIKES
        defaultPostShouldBeFound("noOfLikes.in=" + DEFAULT_NO_OF_LIKES + "," + UPDATED_NO_OF_LIKES);

        // Get all the postList where noOfLikes equals to UPDATED_NO_OF_LIKES
        defaultPostShouldNotBeFound("noOfLikes.in=" + UPDATED_NO_OF_LIKES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes is not null
        defaultPostShouldBeFound("noOfLikes.specified=true");

        // Get all the postList where noOfLikes is null
        defaultPostShouldNotBeFound("noOfLikes.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes is greater than or equal to DEFAULT_NO_OF_LIKES
        defaultPostShouldBeFound("noOfLikes.greaterThanOrEqual=" + DEFAULT_NO_OF_LIKES);

        // Get all the postList where noOfLikes is greater than or equal to UPDATED_NO_OF_LIKES
        defaultPostShouldNotBeFound("noOfLikes.greaterThanOrEqual=" + UPDATED_NO_OF_LIKES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes is less than or equal to DEFAULT_NO_OF_LIKES
        defaultPostShouldBeFound("noOfLikes.lessThanOrEqual=" + DEFAULT_NO_OF_LIKES);

        // Get all the postList where noOfLikes is less than or equal to SMALLER_NO_OF_LIKES
        defaultPostShouldNotBeFound("noOfLikes.lessThanOrEqual=" + SMALLER_NO_OF_LIKES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes is less than DEFAULT_NO_OF_LIKES
        defaultPostShouldNotBeFound("noOfLikes.lessThan=" + DEFAULT_NO_OF_LIKES);

        // Get all the postList where noOfLikes is less than UPDATED_NO_OF_LIKES
        defaultPostShouldBeFound("noOfLikes.lessThan=" + UPDATED_NO_OF_LIKES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfLikesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfLikes is greater than DEFAULT_NO_OF_LIKES
        defaultPostShouldNotBeFound("noOfLikes.greaterThan=" + DEFAULT_NO_OF_LIKES);

        // Get all the postList where noOfLikes is greater than SMALLER_NO_OF_LIKES
        defaultPostShouldBeFound("noOfLikes.greaterThan=" + SMALLER_NO_OF_LIKES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares equals to DEFAULT_NO_OF_SHARES
        defaultPostShouldBeFound("noOfShares.equals=" + DEFAULT_NO_OF_SHARES);

        // Get all the postList where noOfShares equals to UPDATED_NO_OF_SHARES
        defaultPostShouldNotBeFound("noOfShares.equals=" + UPDATED_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares not equals to DEFAULT_NO_OF_SHARES
        defaultPostShouldNotBeFound("noOfShares.notEquals=" + DEFAULT_NO_OF_SHARES);

        // Get all the postList where noOfShares not equals to UPDATED_NO_OF_SHARES
        defaultPostShouldBeFound("noOfShares.notEquals=" + UPDATED_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares in DEFAULT_NO_OF_SHARES or UPDATED_NO_OF_SHARES
        defaultPostShouldBeFound("noOfShares.in=" + DEFAULT_NO_OF_SHARES + "," + UPDATED_NO_OF_SHARES);

        // Get all the postList where noOfShares equals to UPDATED_NO_OF_SHARES
        defaultPostShouldNotBeFound("noOfShares.in=" + UPDATED_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares is not null
        defaultPostShouldBeFound("noOfShares.specified=true");

        // Get all the postList where noOfShares is null
        defaultPostShouldNotBeFound("noOfShares.specified=false");
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares is greater than or equal to DEFAULT_NO_OF_SHARES
        defaultPostShouldBeFound("noOfShares.greaterThanOrEqual=" + DEFAULT_NO_OF_SHARES);

        // Get all the postList where noOfShares is greater than or equal to UPDATED_NO_OF_SHARES
        defaultPostShouldNotBeFound("noOfShares.greaterThanOrEqual=" + UPDATED_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares is less than or equal to DEFAULT_NO_OF_SHARES
        defaultPostShouldBeFound("noOfShares.lessThanOrEqual=" + DEFAULT_NO_OF_SHARES);

        // Get all the postList where noOfShares is less than or equal to SMALLER_NO_OF_SHARES
        defaultPostShouldNotBeFound("noOfShares.lessThanOrEqual=" + SMALLER_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares is less than DEFAULT_NO_OF_SHARES
        defaultPostShouldNotBeFound("noOfShares.lessThan=" + DEFAULT_NO_OF_SHARES);

        // Get all the postList where noOfShares is less than UPDATED_NO_OF_SHARES
        defaultPostShouldBeFound("noOfShares.lessThan=" + UPDATED_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void getAllPostsByNoOfSharesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where noOfShares is greater than DEFAULT_NO_OF_SHARES
        defaultPostShouldNotBeFound("noOfShares.greaterThan=" + DEFAULT_NO_OF_SHARES);

        // Get all the postList where noOfShares is greater than SMALLER_NO_OF_SHARES
        defaultPostShouldBeFound("noOfShares.greaterThan=" + SMALLER_NO_OF_SHARES);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostShouldBeFound(String filter) throws Exception {
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].imageUrlContentType").value(hasItem(DEFAULT_IMAGE_URL_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_URL))))
            .andExpect(jsonPath("$.[*].publishedBy").value(hasItem(DEFAULT_PUBLISHED_BY)))
            .andExpect(jsonPath("$.[*].noOfLikes").value(hasItem(DEFAULT_NO_OF_LIKES)))
            .andExpect(jsonPath("$.[*].noOfShares").value(hasItem(DEFAULT_NO_OF_SHARES)));

        // Check, that the count call also returns 1
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostShouldNotBeFound(String filter) throws Exception {
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .content(UPDATED_CONTENT)
            .imageUrl(UPDATED_IMAGE_URL)
            .imageUrlContentType(UPDATED_IMAGE_URL_CONTENT_TYPE)
            .publishedBy(UPDATED_PUBLISHED_BY)
            .noOfLikes(UPDATED_NO_OF_LIKES)
            .noOfShares(UPDATED_NO_OF_SHARES);

        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPost.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testPost.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testPost.getImageUrlContentType()).isEqualTo(UPDATED_IMAGE_URL_CONTENT_TYPE);
        assertThat(testPost.getPublishedBy()).isEqualTo(UPDATED_PUBLISHED_BY);
        assertThat(testPost.getNoOfLikes()).isEqualTo(UPDATED_NO_OF_LIKES);
        assertThat(testPost.getNoOfShares()).isEqualTo(UPDATED_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void putNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(post))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(post))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost.noOfLikes(UPDATED_NO_OF_LIKES);

        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testPost.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testPost.getImageUrlContentType()).isEqualTo(DEFAULT_IMAGE_URL_CONTENT_TYPE);
        assertThat(testPost.getPublishedBy()).isEqualTo(DEFAULT_PUBLISHED_BY);
        assertThat(testPost.getNoOfLikes()).isEqualTo(UPDATED_NO_OF_LIKES);
        assertThat(testPost.getNoOfShares()).isEqualTo(DEFAULT_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void fullUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost
            .content(UPDATED_CONTENT)
            .imageUrl(UPDATED_IMAGE_URL)
            .imageUrlContentType(UPDATED_IMAGE_URL_CONTENT_TYPE)
            .publishedBy(UPDATED_PUBLISHED_BY)
            .noOfLikes(UPDATED_NO_OF_LIKES)
            .noOfShares(UPDATED_NO_OF_SHARES);

        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            )
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testPost.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testPost.getImageUrlContentType()).isEqualTo(UPDATED_IMAGE_URL_CONTENT_TYPE);
        assertThat(testPost.getPublishedBy()).isEqualTo(UPDATED_PUBLISHED_BY);
        assertThat(testPost.getNoOfLikes()).isEqualTo(UPDATED_NO_OF_LIKES);
        assertThat(testPost.getNoOfShares()).isEqualTo(UPDATED_NO_OF_SHARES);
    }

    @Test
    @Transactional
    void patchNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, post.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(post))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(post))
            )
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        post.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Delete the post
        restPostMockMvc
            .perform(delete(ENTITY_API_URL_ID, post.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
