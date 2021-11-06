package com.credex.fs.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.credex.fs.digital.IntegrationTest;
import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.repository.ChallengeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChallengeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChallengeResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_ICON_URL = "AAAAAAAAAA";
    private static final String UPDATED_ICON_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_REWARD_AMOUNT = 1L;
    private static final Long UPDATED_REWARD_AMOUNT = 2L;

    private static final String ENTITY_API_URL = "/api/challenges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengeRepository challengeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChallengeMockMvc;

    private Challenge challenge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Challenge createEntity(EntityManager em) {
        Challenge challenge = new Challenge()
            .title(DEFAULT_TITLE)
            .message(DEFAULT_MESSAGE)
            .iconUrl(DEFAULT_ICON_URL)
            .rewardAmount(DEFAULT_REWARD_AMOUNT);
        return challenge;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Challenge createUpdatedEntity(EntityManager em) {
        Challenge challenge = new Challenge()
            .title(UPDATED_TITLE)
            .message(UPDATED_MESSAGE)
            .iconUrl(UPDATED_ICON_URL)
            .rewardAmount(UPDATED_REWARD_AMOUNT);
        return challenge;
    }

    @BeforeEach
    public void initTest() {
        challenge = createEntity(em);
    }

    @Test
    @Transactional
    void createChallenge() throws Exception {
        int databaseSizeBeforeCreate = challengeRepository.findAll().size();
        // Create the Challenge
        restChallengeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(challenge)))
            .andExpect(status().isCreated());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeCreate + 1);
        Challenge testChallenge = challengeList.get(challengeList.size() - 1);
        assertThat(testChallenge.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testChallenge.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testChallenge.getIconUrl()).isEqualTo(DEFAULT_ICON_URL);
        assertThat(testChallenge.getRewardAmount()).isEqualTo(DEFAULT_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void createChallengeWithExistingId() throws Exception {
        // Create the Challenge with an existing ID
        challenge.setId(1L);

        int databaseSizeBeforeCreate = challengeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChallengeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(challenge)))
            .andExpect(status().isBadRequest());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllChallenges() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList
        restChallengeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(challenge.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].iconUrl").value(hasItem(DEFAULT_ICON_URL)))
            .andExpect(jsonPath("$.[*].rewardAmount").value(hasItem(DEFAULT_REWARD_AMOUNT.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChallengesWithEagerRelationshipsIsEnabled() throws Exception {
        when(challengeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChallengeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(challengeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChallengesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(challengeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChallengeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(challengeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getChallenge() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get the challenge
        restChallengeMockMvc
            .perform(get(ENTITY_API_URL_ID, challenge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(challenge.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.iconUrl").value(DEFAULT_ICON_URL))
            .andExpect(jsonPath("$.rewardAmount").value(DEFAULT_REWARD_AMOUNT.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingChallenge() throws Exception {
        // Get the challenge
        restChallengeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewChallenge() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();

        // Update the challenge
        Challenge updatedChallenge = challengeRepository.findById(challenge.getId()).get();
        // Disconnect from session so that the updates on updatedChallenge are not directly saved in db
        em.detach(updatedChallenge);
        updatedChallenge.title(UPDATED_TITLE).message(UPDATED_MESSAGE).iconUrl(UPDATED_ICON_URL).rewardAmount(UPDATED_REWARD_AMOUNT);

        restChallengeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedChallenge.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedChallenge))
            )
            .andExpect(status().isOk());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
        Challenge testChallenge = challengeList.get(challengeList.size() - 1);
        assertThat(testChallenge.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testChallenge.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testChallenge.getIconUrl()).isEqualTo(UPDATED_ICON_URL);
        assertThat(testChallenge.getRewardAmount()).isEqualTo(UPDATED_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void putNonExistingChallenge() throws Exception {
        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();
        challenge.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChallengeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, challenge.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(challenge))
            )
            .andExpect(status().isBadRequest());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChallenge() throws Exception {
        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();
        challenge.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChallengeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(challenge))
            )
            .andExpect(status().isBadRequest());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChallenge() throws Exception {
        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();
        challenge.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChallengeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(challenge)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChallengeWithPatch() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();

        // Update the challenge using partial update
        Challenge partialUpdatedChallenge = new Challenge();
        partialUpdatedChallenge.setId(challenge.getId());

        partialUpdatedChallenge.message(UPDATED_MESSAGE);

        restChallengeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChallenge.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedChallenge))
            )
            .andExpect(status().isOk());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
        Challenge testChallenge = challengeList.get(challengeList.size() - 1);
        assertThat(testChallenge.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testChallenge.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testChallenge.getIconUrl()).isEqualTo(DEFAULT_ICON_URL);
        assertThat(testChallenge.getRewardAmount()).isEqualTo(DEFAULT_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateChallengeWithPatch() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();

        // Update the challenge using partial update
        Challenge partialUpdatedChallenge = new Challenge();
        partialUpdatedChallenge.setId(challenge.getId());

        partialUpdatedChallenge.title(UPDATED_TITLE).message(UPDATED_MESSAGE).iconUrl(UPDATED_ICON_URL).rewardAmount(UPDATED_REWARD_AMOUNT);

        restChallengeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChallenge.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedChallenge))
            )
            .andExpect(status().isOk());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
        Challenge testChallenge = challengeList.get(challengeList.size() - 1);
        assertThat(testChallenge.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testChallenge.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testChallenge.getIconUrl()).isEqualTo(UPDATED_ICON_URL);
        assertThat(testChallenge.getRewardAmount()).isEqualTo(UPDATED_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingChallenge() throws Exception {
        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();
        challenge.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChallengeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, challenge.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(challenge))
            )
            .andExpect(status().isBadRequest());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChallenge() throws Exception {
        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();
        challenge.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChallengeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(challenge))
            )
            .andExpect(status().isBadRequest());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChallenge() throws Exception {
        int databaseSizeBeforeUpdate = challengeRepository.findAll().size();
        challenge.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChallengeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(challenge))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Challenge in the database
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChallenge() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        int databaseSizeBeforeDelete = challengeRepository.findAll().size();

        // Delete the challenge
        restChallengeMockMvc
            .perform(delete(ENTITY_API_URL_ID, challenge.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Challenge> challengeList = challengeRepository.findAll();
        assertThat(challengeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
