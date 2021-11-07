package com.hackaton.smarthack.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hackaton.smarthack.IntegrationTest;
import com.hackaton.smarthack.domain.Challenge;
import com.hackaton.smarthack.domain.HashTag;
import com.hackaton.smarthack.repository.HashTagRepository;
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

/**
 * Integration tests for the {@link HashTagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HashTagResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/hash-tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HashTagRepository hashTagRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHashTagMockMvc;

    private HashTag hashTag;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HashTag createEntity(EntityManager em) {
        HashTag hashTag = new HashTag().name(DEFAULT_NAME).company(DEFAULT_COMPANY);
        return hashTag;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HashTag createUpdatedEntity(EntityManager em) {
        HashTag hashTag = new HashTag().name(UPDATED_NAME).company(UPDATED_COMPANY);
        return hashTag;
    }

    @BeforeEach
    public void initTest() {
        hashTag = createEntity(em);
    }

    @Test
    @Transactional
    void createHashTag() throws Exception {
        int databaseSizeBeforeCreate = hashTagRepository.findAll().size();
        // Create the HashTag
        restHashTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hashTag)))
            .andExpect(status().isCreated());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeCreate + 1);
        HashTag testHashTag = hashTagList.get(hashTagList.size() - 1);
        assertThat(testHashTag.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testHashTag.getCompany()).isEqualTo(DEFAULT_COMPANY);
    }

    @Test
    @Transactional
    void createHashTagWithExistingId() throws Exception {
        // Create the HashTag with an existing ID
        hashTag.setId(1L);

        int databaseSizeBeforeCreate = hashTagRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHashTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hashTag)))
            .andExpect(status().isBadRequest());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHashTags() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList
        restHashTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hashTag.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)));
    }

    @Test
    @Transactional
    void getHashTag() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get the hashTag
        restHashTagMockMvc
            .perform(get(ENTITY_API_URL_ID, hashTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hashTag.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.company").value(DEFAULT_COMPANY));
    }

    @Test
    @Transactional
    void getHashTagsByIdFiltering() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        Long id = hashTag.getId();

        defaultHashTagShouldBeFound("id.equals=" + id);
        defaultHashTagShouldNotBeFound("id.notEquals=" + id);

        defaultHashTagShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultHashTagShouldNotBeFound("id.greaterThan=" + id);

        defaultHashTagShouldBeFound("id.lessThanOrEqual=" + id);
        defaultHashTagShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHashTagsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where name equals to DEFAULT_NAME
        defaultHashTagShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the hashTagList where name equals to UPDATED_NAME
        defaultHashTagShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHashTagsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where name not equals to DEFAULT_NAME
        defaultHashTagShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the hashTagList where name not equals to UPDATED_NAME
        defaultHashTagShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHashTagsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where name in DEFAULT_NAME or UPDATED_NAME
        defaultHashTagShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the hashTagList where name equals to UPDATED_NAME
        defaultHashTagShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHashTagsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where name is not null
        defaultHashTagShouldBeFound("name.specified=true");

        // Get all the hashTagList where name is null
        defaultHashTagShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllHashTagsByNameContainsSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where name contains DEFAULT_NAME
        defaultHashTagShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the hashTagList where name contains UPDATED_NAME
        defaultHashTagShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHashTagsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where name does not contain DEFAULT_NAME
        defaultHashTagShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the hashTagList where name does not contain UPDATED_NAME
        defaultHashTagShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllHashTagsByCompanyIsEqualToSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where company equals to DEFAULT_COMPANY
        defaultHashTagShouldBeFound("company.equals=" + DEFAULT_COMPANY);

        // Get all the hashTagList where company equals to UPDATED_COMPANY
        defaultHashTagShouldNotBeFound("company.equals=" + UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void getAllHashTagsByCompanyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where company not equals to DEFAULT_COMPANY
        defaultHashTagShouldNotBeFound("company.notEquals=" + DEFAULT_COMPANY);

        // Get all the hashTagList where company not equals to UPDATED_COMPANY
        defaultHashTagShouldBeFound("company.notEquals=" + UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void getAllHashTagsByCompanyIsInShouldWork() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where company in DEFAULT_COMPANY or UPDATED_COMPANY
        defaultHashTagShouldBeFound("company.in=" + DEFAULT_COMPANY + "," + UPDATED_COMPANY);

        // Get all the hashTagList where company equals to UPDATED_COMPANY
        defaultHashTagShouldNotBeFound("company.in=" + UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void getAllHashTagsByCompanyIsNullOrNotNull() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where company is not null
        defaultHashTagShouldBeFound("company.specified=true");

        // Get all the hashTagList where company is null
        defaultHashTagShouldNotBeFound("company.specified=false");
    }

    @Test
    @Transactional
    void getAllHashTagsByCompanyContainsSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where company contains DEFAULT_COMPANY
        defaultHashTagShouldBeFound("company.contains=" + DEFAULT_COMPANY);

        // Get all the hashTagList where company contains UPDATED_COMPANY
        defaultHashTagShouldNotBeFound("company.contains=" + UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void getAllHashTagsByCompanyNotContainsSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        // Get all the hashTagList where company does not contain DEFAULT_COMPANY
        defaultHashTagShouldNotBeFound("company.doesNotContain=" + DEFAULT_COMPANY);

        // Get all the hashTagList where company does not contain UPDATED_COMPANY
        defaultHashTagShouldBeFound("company.doesNotContain=" + UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void getAllHashTagsByChallengesIsEqualToSomething() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);
        Challenge challenges;
        if (TestUtil.findAll(em, Challenge.class).isEmpty()) {
            challenges = ChallengeResourceIT.createEntity(em);
            em.persist(challenges);
            em.flush();
        } else {
            challenges = TestUtil.findAll(em, Challenge.class).get(0);
        }
        em.persist(challenges);
        em.flush();
        hashTag.addChallenges(challenges);
        hashTagRepository.saveAndFlush(hashTag);
        Long challengesId = challenges.getId();

        // Get all the hashTagList where challenges equals to challengesId
        defaultHashTagShouldBeFound("challengesId.equals=" + challengesId);

        // Get all the hashTagList where challenges equals to (challengesId + 1)
        defaultHashTagShouldNotBeFound("challengesId.equals=" + (challengesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHashTagShouldBeFound(String filter) throws Exception {
        restHashTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hashTag.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)));

        // Check, that the count call also returns 1
        restHashTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHashTagShouldNotBeFound(String filter) throws Exception {
        restHashTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHashTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHashTag() throws Exception {
        // Get the hashTag
        restHashTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHashTag() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();

        // Update the hashTag
        HashTag updatedHashTag = hashTagRepository.findById(hashTag.getId()).get();
        // Disconnect from session so that the updates on updatedHashTag are not directly saved in db
        em.detach(updatedHashTag);
        updatedHashTag.name(UPDATED_NAME).company(UPDATED_COMPANY);

        restHashTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHashTag.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHashTag))
            )
            .andExpect(status().isOk());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
        HashTag testHashTag = hashTagList.get(hashTagList.size() - 1);
        assertThat(testHashTag.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testHashTag.getCompany()).isEqualTo(UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void putNonExistingHashTag() throws Exception {
        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();
        hashTag.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHashTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hashTag.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hashTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHashTag() throws Exception {
        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();
        hashTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHashTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hashTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHashTag() throws Exception {
        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();
        hashTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHashTagMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hashTag)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHashTagWithPatch() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();

        // Update the hashTag using partial update
        HashTag partialUpdatedHashTag = new HashTag();
        partialUpdatedHashTag.setId(hashTag.getId());

        partialUpdatedHashTag.name(UPDATED_NAME).company(UPDATED_COMPANY);

        restHashTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHashTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHashTag))
            )
            .andExpect(status().isOk());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
        HashTag testHashTag = hashTagList.get(hashTagList.size() - 1);
        assertThat(testHashTag.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testHashTag.getCompany()).isEqualTo(UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void fullUpdateHashTagWithPatch() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();

        // Update the hashTag using partial update
        HashTag partialUpdatedHashTag = new HashTag();
        partialUpdatedHashTag.setId(hashTag.getId());

        partialUpdatedHashTag.name(UPDATED_NAME).company(UPDATED_COMPANY);

        restHashTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHashTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHashTag))
            )
            .andExpect(status().isOk());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
        HashTag testHashTag = hashTagList.get(hashTagList.size() - 1);
        assertThat(testHashTag.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testHashTag.getCompany()).isEqualTo(UPDATED_COMPANY);
    }

    @Test
    @Transactional
    void patchNonExistingHashTag() throws Exception {
        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();
        hashTag.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHashTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hashTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hashTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHashTag() throws Exception {
        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();
        hashTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHashTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hashTag))
            )
            .andExpect(status().isBadRequest());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHashTag() throws Exception {
        int databaseSizeBeforeUpdate = hashTagRepository.findAll().size();
        hashTag.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHashTagMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(hashTag)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HashTag in the database
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHashTag() throws Exception {
        // Initialize the database
        hashTagRepository.saveAndFlush(hashTag);

        int databaseSizeBeforeDelete = hashTagRepository.findAll().size();

        // Delete the hashTag
        restHashTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, hashTag.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HashTag> hashTagList = hashTagRepository.findAll();
        assertThat(hashTagList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
