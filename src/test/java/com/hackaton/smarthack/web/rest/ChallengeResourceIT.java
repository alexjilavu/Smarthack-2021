package com.hackaton.smarthack.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hackaton.smarthack.IntegrationTest;
import com.hackaton.smarthack.domain.AppUser;
import com.hackaton.smarthack.domain.Challenge;
import com.hackaton.smarthack.domain.HashTag;
import com.hackaton.smarthack.domain.Icon;
import com.hackaton.smarthack.repository.ChallengeRepository;
import com.hackaton.smarthack.service.ChallengeService;
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
    private static final Long SMALLER_REWARD_AMOUNT = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/challenges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChallengeRepository challengeRepository;

    @Mock
    private ChallengeRepository challengeRepositoryMock;

    @Mock
    private ChallengeService challengeServiceMock;

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
        when(challengeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChallengeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(challengeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChallengesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(challengeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChallengeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(challengeServiceMock, times(1)).findAllWithEagerRelationships(any());
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
    void getChallengesByIdFiltering() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        Long id = challenge.getId();

        defaultChallengeShouldBeFound("id.equals=" + id);
        defaultChallengeShouldNotBeFound("id.notEquals=" + id);

        defaultChallengeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultChallengeShouldNotBeFound("id.greaterThan=" + id);

        defaultChallengeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultChallengeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllChallengesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where title equals to DEFAULT_TITLE
        defaultChallengeShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the challengeList where title equals to UPDATED_TITLE
        defaultChallengeShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChallengesByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where title not equals to DEFAULT_TITLE
        defaultChallengeShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the challengeList where title not equals to UPDATED_TITLE
        defaultChallengeShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChallengesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultChallengeShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the challengeList where title equals to UPDATED_TITLE
        defaultChallengeShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChallengesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where title is not null
        defaultChallengeShouldBeFound("title.specified=true");

        // Get all the challengeList where title is null
        defaultChallengeShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllChallengesByTitleContainsSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where title contains DEFAULT_TITLE
        defaultChallengeShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the challengeList where title contains UPDATED_TITLE
        defaultChallengeShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChallengesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where title does not contain DEFAULT_TITLE
        defaultChallengeShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the challengeList where title does not contain UPDATED_TITLE
        defaultChallengeShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllChallengesByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where message equals to DEFAULT_MESSAGE
        defaultChallengeShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the challengeList where message equals to UPDATED_MESSAGE
        defaultChallengeShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChallengesByMessageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where message not equals to DEFAULT_MESSAGE
        defaultChallengeShouldNotBeFound("message.notEquals=" + DEFAULT_MESSAGE);

        // Get all the challengeList where message not equals to UPDATED_MESSAGE
        defaultChallengeShouldBeFound("message.notEquals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChallengesByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultChallengeShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the challengeList where message equals to UPDATED_MESSAGE
        defaultChallengeShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChallengesByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where message is not null
        defaultChallengeShouldBeFound("message.specified=true");

        // Get all the challengeList where message is null
        defaultChallengeShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllChallengesByMessageContainsSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where message contains DEFAULT_MESSAGE
        defaultChallengeShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the challengeList where message contains UPDATED_MESSAGE
        defaultChallengeShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChallengesByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where message does not contain DEFAULT_MESSAGE
        defaultChallengeShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the challengeList where message does not contain UPDATED_MESSAGE
        defaultChallengeShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChallengesByIconUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where iconUrl equals to DEFAULT_ICON_URL
        defaultChallengeShouldBeFound("iconUrl.equals=" + DEFAULT_ICON_URL);

        // Get all the challengeList where iconUrl equals to UPDATED_ICON_URL
        defaultChallengeShouldNotBeFound("iconUrl.equals=" + UPDATED_ICON_URL);
    }

    @Test
    @Transactional
    void getAllChallengesByIconUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where iconUrl not equals to DEFAULT_ICON_URL
        defaultChallengeShouldNotBeFound("iconUrl.notEquals=" + DEFAULT_ICON_URL);

        // Get all the challengeList where iconUrl not equals to UPDATED_ICON_URL
        defaultChallengeShouldBeFound("iconUrl.notEquals=" + UPDATED_ICON_URL);
    }

    @Test
    @Transactional
    void getAllChallengesByIconUrlIsInShouldWork() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where iconUrl in DEFAULT_ICON_URL or UPDATED_ICON_URL
        defaultChallengeShouldBeFound("iconUrl.in=" + DEFAULT_ICON_URL + "," + UPDATED_ICON_URL);

        // Get all the challengeList where iconUrl equals to UPDATED_ICON_URL
        defaultChallengeShouldNotBeFound("iconUrl.in=" + UPDATED_ICON_URL);
    }

    @Test
    @Transactional
    void getAllChallengesByIconUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where iconUrl is not null
        defaultChallengeShouldBeFound("iconUrl.specified=true");

        // Get all the challengeList where iconUrl is null
        defaultChallengeShouldNotBeFound("iconUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllChallengesByIconUrlContainsSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where iconUrl contains DEFAULT_ICON_URL
        defaultChallengeShouldBeFound("iconUrl.contains=" + DEFAULT_ICON_URL);

        // Get all the challengeList where iconUrl contains UPDATED_ICON_URL
        defaultChallengeShouldNotBeFound("iconUrl.contains=" + UPDATED_ICON_URL);
    }

    @Test
    @Transactional
    void getAllChallengesByIconUrlNotContainsSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where iconUrl does not contain DEFAULT_ICON_URL
        defaultChallengeShouldNotBeFound("iconUrl.doesNotContain=" + DEFAULT_ICON_URL);

        // Get all the challengeList where iconUrl does not contain UPDATED_ICON_URL
        defaultChallengeShouldBeFound("iconUrl.doesNotContain=" + UPDATED_ICON_URL);
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount equals to DEFAULT_REWARD_AMOUNT
        defaultChallengeShouldBeFound("rewardAmount.equals=" + DEFAULT_REWARD_AMOUNT);

        // Get all the challengeList where rewardAmount equals to UPDATED_REWARD_AMOUNT
        defaultChallengeShouldNotBeFound("rewardAmount.equals=" + UPDATED_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount not equals to DEFAULT_REWARD_AMOUNT
        defaultChallengeShouldNotBeFound("rewardAmount.notEquals=" + DEFAULT_REWARD_AMOUNT);

        // Get all the challengeList where rewardAmount not equals to UPDATED_REWARD_AMOUNT
        defaultChallengeShouldBeFound("rewardAmount.notEquals=" + UPDATED_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsInShouldWork() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount in DEFAULT_REWARD_AMOUNT or UPDATED_REWARD_AMOUNT
        defaultChallengeShouldBeFound("rewardAmount.in=" + DEFAULT_REWARD_AMOUNT + "," + UPDATED_REWARD_AMOUNT);

        // Get all the challengeList where rewardAmount equals to UPDATED_REWARD_AMOUNT
        defaultChallengeShouldNotBeFound("rewardAmount.in=" + UPDATED_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount is not null
        defaultChallengeShouldBeFound("rewardAmount.specified=true");

        // Get all the challengeList where rewardAmount is null
        defaultChallengeShouldNotBeFound("rewardAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount is greater than or equal to DEFAULT_REWARD_AMOUNT
        defaultChallengeShouldBeFound("rewardAmount.greaterThanOrEqual=" + DEFAULT_REWARD_AMOUNT);

        // Get all the challengeList where rewardAmount is greater than or equal to UPDATED_REWARD_AMOUNT
        defaultChallengeShouldNotBeFound("rewardAmount.greaterThanOrEqual=" + UPDATED_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount is less than or equal to DEFAULT_REWARD_AMOUNT
        defaultChallengeShouldBeFound("rewardAmount.lessThanOrEqual=" + DEFAULT_REWARD_AMOUNT);

        // Get all the challengeList where rewardAmount is less than or equal to SMALLER_REWARD_AMOUNT
        defaultChallengeShouldNotBeFound("rewardAmount.lessThanOrEqual=" + SMALLER_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount is less than DEFAULT_REWARD_AMOUNT
        defaultChallengeShouldNotBeFound("rewardAmount.lessThan=" + DEFAULT_REWARD_AMOUNT);

        // Get all the challengeList where rewardAmount is less than UPDATED_REWARD_AMOUNT
        defaultChallengeShouldBeFound("rewardAmount.lessThan=" + UPDATED_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void getAllChallengesByRewardAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);

        // Get all the challengeList where rewardAmount is greater than DEFAULT_REWARD_AMOUNT
        defaultChallengeShouldNotBeFound("rewardAmount.greaterThan=" + DEFAULT_REWARD_AMOUNT);

        // Get all the challengeList where rewardAmount is greater than SMALLER_REWARD_AMOUNT
        defaultChallengeShouldBeFound("rewardAmount.greaterThan=" + SMALLER_REWARD_AMOUNT);
    }

    @Test
    @Transactional
    void getAllChallengesByIconIsEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);
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
        challenge.setIcon(icon);
        challengeRepository.saveAndFlush(challenge);
        Long iconId = icon.getId();

        // Get all the challengeList where icon equals to iconId
        defaultChallengeShouldBeFound("iconId.equals=" + iconId);

        // Get all the challengeList where icon equals to (iconId + 1)
        defaultChallengeShouldNotBeFound("iconId.equals=" + (iconId + 1));
    }

    @Test
    @Transactional
    void getAllChallengesByHashTagsIsEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);
        HashTag hashTags;
        if (TestUtil.findAll(em, HashTag.class).isEmpty()) {
            hashTags = HashTagResourceIT.createEntity(em);
            em.persist(hashTags);
            em.flush();
        } else {
            hashTags = TestUtil.findAll(em, HashTag.class).get(0);
        }
        em.persist(hashTags);
        em.flush();
        challenge.addHashTags(hashTags);
        challengeRepository.saveAndFlush(challenge);
        Long hashTagsId = hashTags.getId();

        // Get all the challengeList where hashTags equals to hashTagsId
        defaultChallengeShouldBeFound("hashTagsId.equals=" + hashTagsId);

        // Get all the challengeList where hashTags equals to (hashTagsId + 1)
        defaultChallengeShouldNotBeFound("hashTagsId.equals=" + (hashTagsId + 1));
    }

    @Test
    @Transactional
    void getAllChallengesByUsersThatCompletedIsEqualToSomething() throws Exception {
        // Initialize the database
        challengeRepository.saveAndFlush(challenge);
        AppUser usersThatCompleted;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            usersThatCompleted = AppUserResourceIT.createEntity(em);
            em.persist(usersThatCompleted);
            em.flush();
        } else {
            usersThatCompleted = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(usersThatCompleted);
        em.flush();
        challenge.addUsersThatCompleted(usersThatCompleted);
        challengeRepository.saveAndFlush(challenge);
        Long usersThatCompletedId = usersThatCompleted.getId();

        // Get all the challengeList where usersThatCompleted equals to usersThatCompletedId
        defaultChallengeShouldBeFound("usersThatCompletedId.equals=" + usersThatCompletedId);

        // Get all the challengeList where usersThatCompleted equals to (usersThatCompletedId + 1)
        defaultChallengeShouldNotBeFound("usersThatCompletedId.equals=" + (usersThatCompletedId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultChallengeShouldBeFound(String filter) throws Exception {
        restChallengeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(challenge.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].iconUrl").value(hasItem(DEFAULT_ICON_URL)))
            .andExpect(jsonPath("$.[*].rewardAmount").value(hasItem(DEFAULT_REWARD_AMOUNT.intValue())));

        // Check, that the count call also returns 1
        restChallengeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultChallengeShouldNotBeFound(String filter) throws Exception {
        restChallengeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restChallengeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
