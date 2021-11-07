package com.hackaton.smarthack.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hackaton.smarthack.IntegrationTest;
import com.hackaton.smarthack.domain.Company;
import com.hackaton.smarthack.domain.Icon;
import com.hackaton.smarthack.domain.Reward;
import com.hackaton.smarthack.repository.RewardRepository;
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
 * Integration tests for the {@link RewardResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RewardResourceIT {

    private static final Long DEFAULT_VALUE = 1L;
    private static final Long UPDATED_VALUE = 2L;
    private static final Long SMALLER_VALUE = 1L - 1L;

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rewards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRewardMockMvc;

    private Reward reward;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reward createEntity(EntityManager em) {
        Reward reward = new Reward().value(DEFAULT_VALUE).content(DEFAULT_CONTENT);
        return reward;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reward createUpdatedEntity(EntityManager em) {
        Reward reward = new Reward().value(UPDATED_VALUE).content(UPDATED_CONTENT);
        return reward;
    }

    @BeforeEach
    public void initTest() {
        reward = createEntity(em);
    }

    @Test
    @Transactional
    void createReward() throws Exception {
        int databaseSizeBeforeCreate = rewardRepository.findAll().size();
        // Create the Reward
        restRewardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reward)))
            .andExpect(status().isCreated());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeCreate + 1);
        Reward testReward = rewardList.get(rewardList.size() - 1);
        assertThat(testReward.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testReward.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void createRewardWithExistingId() throws Exception {
        // Create the Reward with an existing ID
        reward.setId(1L);

        int databaseSizeBeforeCreate = rewardRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRewardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reward)))
            .andExpect(status().isBadRequest());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRewards() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList
        restRewardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reward.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    void getReward() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get the reward
        restRewardMockMvc
            .perform(get(ENTITY_API_URL_ID, reward.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reward.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }

    @Test
    @Transactional
    void getRewardsByIdFiltering() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        Long id = reward.getId();

        defaultRewardShouldBeFound("id.equals=" + id);
        defaultRewardShouldNotBeFound("id.notEquals=" + id);

        defaultRewardShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRewardShouldNotBeFound("id.greaterThan=" + id);

        defaultRewardShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRewardShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value equals to DEFAULT_VALUE
        defaultRewardShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the rewardList where value equals to UPDATED_VALUE
        defaultRewardShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value not equals to DEFAULT_VALUE
        defaultRewardShouldNotBeFound("value.notEquals=" + DEFAULT_VALUE);

        // Get all the rewardList where value not equals to UPDATED_VALUE
        defaultRewardShouldBeFound("value.notEquals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultRewardShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the rewardList where value equals to UPDATED_VALUE
        defaultRewardShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value is not null
        defaultRewardShouldBeFound("value.specified=true");

        // Get all the rewardList where value is null
        defaultRewardShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value is greater than or equal to DEFAULT_VALUE
        defaultRewardShouldBeFound("value.greaterThanOrEqual=" + DEFAULT_VALUE);

        // Get all the rewardList where value is greater than or equal to UPDATED_VALUE
        defaultRewardShouldNotBeFound("value.greaterThanOrEqual=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value is less than or equal to DEFAULT_VALUE
        defaultRewardShouldBeFound("value.lessThanOrEqual=" + DEFAULT_VALUE);

        // Get all the rewardList where value is less than or equal to SMALLER_VALUE
        defaultRewardShouldNotBeFound("value.lessThanOrEqual=" + SMALLER_VALUE);
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsLessThanSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value is less than DEFAULT_VALUE
        defaultRewardShouldNotBeFound("value.lessThan=" + DEFAULT_VALUE);

        // Get all the rewardList where value is less than UPDATED_VALUE
        defaultRewardShouldBeFound("value.lessThan=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllRewardsByValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where value is greater than DEFAULT_VALUE
        defaultRewardShouldNotBeFound("value.greaterThan=" + DEFAULT_VALUE);

        // Get all the rewardList where value is greater than SMALLER_VALUE
        defaultRewardShouldBeFound("value.greaterThan=" + SMALLER_VALUE);
    }

    @Test
    @Transactional
    void getAllRewardsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where content equals to DEFAULT_CONTENT
        defaultRewardShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the rewardList where content equals to UPDATED_CONTENT
        defaultRewardShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllRewardsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where content not equals to DEFAULT_CONTENT
        defaultRewardShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the rewardList where content not equals to UPDATED_CONTENT
        defaultRewardShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllRewardsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultRewardShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the rewardList where content equals to UPDATED_CONTENT
        defaultRewardShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllRewardsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where content is not null
        defaultRewardShouldBeFound("content.specified=true");

        // Get all the rewardList where content is null
        defaultRewardShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    void getAllRewardsByContentContainsSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where content contains DEFAULT_CONTENT
        defaultRewardShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the rewardList where content contains UPDATED_CONTENT
        defaultRewardShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllRewardsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        // Get all the rewardList where content does not contain DEFAULT_CONTENT
        defaultRewardShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the rewardList where content does not contain UPDATED_CONTENT
        defaultRewardShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllRewardsByIconIsEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);
        Icon icon;
        if (TestUtil.findAll(em, Icon.class).isEmpty()) {
            icon = IconResourceIT.createEntity(em);
            em.persist(icon);
            em.flush();
        } else {
            icon = TestUtil.findAll(em, Icon.class).get(0);
        }
        em.persist(icon);
        em.flush();
        reward.setIcon(icon);
        rewardRepository.saveAndFlush(reward);
        Long iconId = icon.getId();

        // Get all the rewardList where icon equals to iconId
        defaultRewardShouldBeFound("iconId.equals=" + iconId);

        // Get all the rewardList where icon equals to (iconId + 1)
        defaultRewardShouldNotBeFound("iconId.equals=" + (iconId + 1));
    }

    @Test
    @Transactional
    void getAllRewardsByCompanyIsEqualToSomething() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            company = CompanyResourceIT.createEntity(em);
            em.persist(company);
            em.flush();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        reward.setCompany(company);
        rewardRepository.saveAndFlush(reward);
        Long companyId = company.getId();

        // Get all the rewardList where company equals to companyId
        defaultRewardShouldBeFound("companyId.equals=" + companyId);

        // Get all the rewardList where company equals to (companyId + 1)
        defaultRewardShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRewardShouldBeFound(String filter) throws Exception {
        restRewardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reward.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));

        // Check, that the count call also returns 1
        restRewardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRewardShouldNotBeFound(String filter) throws Exception {
        restRewardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRewardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReward() throws Exception {
        // Get the reward
        restRewardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewReward() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();

        // Update the reward
        Reward updatedReward = rewardRepository.findById(reward.getId()).get();
        // Disconnect from session so that the updates on updatedReward are not directly saved in db
        em.detach(updatedReward);
        updatedReward.value(UPDATED_VALUE).content(UPDATED_CONTENT);

        restRewardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReward.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedReward))
            )
            .andExpect(status().isOk());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
        Reward testReward = rewardList.get(rewardList.size() - 1);
        assertThat(testReward.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testReward.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void putNonExistingReward() throws Exception {
        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();
        reward.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRewardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reward.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reward))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReward() throws Exception {
        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();
        reward.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reward))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReward() throws Exception {
        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();
        reward.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reward)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRewardWithPatch() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();

        // Update the reward using partial update
        Reward partialUpdatedReward = new Reward();
        partialUpdatedReward.setId(reward.getId());

        partialUpdatedReward.value(UPDATED_VALUE);

        restRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReward.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReward))
            )
            .andExpect(status().isOk());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
        Reward testReward = rewardList.get(rewardList.size() - 1);
        assertThat(testReward.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testReward.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void fullUpdateRewardWithPatch() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();

        // Update the reward using partial update
        Reward partialUpdatedReward = new Reward();
        partialUpdatedReward.setId(reward.getId());

        partialUpdatedReward.value(UPDATED_VALUE).content(UPDATED_CONTENT);

        restRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReward.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReward))
            )
            .andExpect(status().isOk());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
        Reward testReward = rewardList.get(rewardList.size() - 1);
        assertThat(testReward.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testReward.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void patchNonExistingReward() throws Exception {
        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();
        reward.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reward.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reward))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReward() throws Exception {
        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();
        reward.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reward))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReward() throws Exception {
        int databaseSizeBeforeUpdate = rewardRepository.findAll().size();
        reward.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRewardMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(reward)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reward in the database
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReward() throws Exception {
        // Initialize the database
        rewardRepository.saveAndFlush(reward);

        int databaseSizeBeforeDelete = rewardRepository.findAll().size();

        // Delete the reward
        restRewardMockMvc
            .perform(delete(ENTITY_API_URL_ID, reward.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
