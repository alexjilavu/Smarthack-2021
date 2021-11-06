package com.credex.fs.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.credex.fs.digital.IntegrationTest;
import com.credex.fs.digital.domain.AppUser;
import com.credex.fs.digital.domain.Challenge;
import com.credex.fs.digital.domain.User;
import com.credex.fs.digital.repository.AppUserRepository;
import com.credex.fs.digital.service.AppUserService;
import com.credex.fs.digital.service.criteria.AppUserCriteria;
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
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AppUserResourceIT {

    private static final String DEFAULT_WALLET_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_WALLET_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_WALLET_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_WALLET_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserRepository appUserRepositoryMock;

    @Mock
    private AppUserService appUserServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppUserMockMvc;

    private AppUser appUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity(EntityManager em) {
        AppUser appUser = new AppUser().walletAddress(DEFAULT_WALLET_ADDRESS).walletPassword(DEFAULT_WALLET_PASSWORD);
        return appUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity(EntityManager em) {
        AppUser appUser = new AppUser().walletAddress(UPDATED_WALLET_ADDRESS).walletPassword(UPDATED_WALLET_PASSWORD);
        return appUser;
    }

    @BeforeEach
    public void initTest() {
        appUser = createEntity(em);
    }

    @Test
    @Transactional
    void createAppUser() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().size();
        // Create the AppUser
        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUser)))
            .andExpect(status().isCreated());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate + 1);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getWalletAddress()).isEqualTo(DEFAULT_WALLET_ADDRESS);
        assertThat(testAppUser.getWalletPassword()).isEqualTo(DEFAULT_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId(1L);

        int databaseSizeBeforeCreate = appUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUser)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAppUsers() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].walletAddress").value(hasItem(DEFAULT_WALLET_ADDRESS)))
            .andExpect(jsonPath("$.[*].walletPassword").value(hasItem(DEFAULT_WALLET_PASSWORD)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get the appUser
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL_ID, appUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appUser.getId().intValue()))
            .andExpect(jsonPath("$.walletAddress").value(DEFAULT_WALLET_ADDRESS))
            .andExpect(jsonPath("$.walletPassword").value(DEFAULT_WALLET_PASSWORD));
    }

    @Test
    @Transactional
    void getAppUsersByIdFiltering() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        Long id = appUser.getId();

        defaultAppUserShouldBeFound("id.equals=" + id);
        defaultAppUserShouldNotBeFound("id.notEquals=" + id);

        defaultAppUserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAppUserShouldNotBeFound("id.greaterThan=" + id);

        defaultAppUserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAppUserShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletAddress equals to DEFAULT_WALLET_ADDRESS
        defaultAppUserShouldBeFound("walletAddress.equals=" + DEFAULT_WALLET_ADDRESS);

        // Get all the appUserList where walletAddress equals to UPDATED_WALLET_ADDRESS
        defaultAppUserShouldNotBeFound("walletAddress.equals=" + UPDATED_WALLET_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletAddress not equals to DEFAULT_WALLET_ADDRESS
        defaultAppUserShouldNotBeFound("walletAddress.notEquals=" + DEFAULT_WALLET_ADDRESS);

        // Get all the appUserList where walletAddress not equals to UPDATED_WALLET_ADDRESS
        defaultAppUserShouldBeFound("walletAddress.notEquals=" + UPDATED_WALLET_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletAddressIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletAddress in DEFAULT_WALLET_ADDRESS or UPDATED_WALLET_ADDRESS
        defaultAppUserShouldBeFound("walletAddress.in=" + DEFAULT_WALLET_ADDRESS + "," + UPDATED_WALLET_ADDRESS);

        // Get all the appUserList where walletAddress equals to UPDATED_WALLET_ADDRESS
        defaultAppUserShouldNotBeFound("walletAddress.in=" + UPDATED_WALLET_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletAddress is not null
        defaultAppUserShouldBeFound("walletAddress.specified=true");

        // Get all the appUserList where walletAddress is null
        defaultAppUserShouldNotBeFound("walletAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletAddressContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletAddress contains DEFAULT_WALLET_ADDRESS
        defaultAppUserShouldBeFound("walletAddress.contains=" + DEFAULT_WALLET_ADDRESS);

        // Get all the appUserList where walletAddress contains UPDATED_WALLET_ADDRESS
        defaultAppUserShouldNotBeFound("walletAddress.contains=" + UPDATED_WALLET_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletAddressNotContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletAddress does not contain DEFAULT_WALLET_ADDRESS
        defaultAppUserShouldNotBeFound("walletAddress.doesNotContain=" + DEFAULT_WALLET_ADDRESS);

        // Get all the appUserList where walletAddress does not contain UPDATED_WALLET_ADDRESS
        defaultAppUserShouldBeFound("walletAddress.doesNotContain=" + UPDATED_WALLET_ADDRESS);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletPasswordIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletPassword equals to DEFAULT_WALLET_PASSWORD
        defaultAppUserShouldBeFound("walletPassword.equals=" + DEFAULT_WALLET_PASSWORD);

        // Get all the appUserList where walletPassword equals to UPDATED_WALLET_PASSWORD
        defaultAppUserShouldNotBeFound("walletPassword.equals=" + UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletPasswordIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletPassword not equals to DEFAULT_WALLET_PASSWORD
        defaultAppUserShouldNotBeFound("walletPassword.notEquals=" + DEFAULT_WALLET_PASSWORD);

        // Get all the appUserList where walletPassword not equals to UPDATED_WALLET_PASSWORD
        defaultAppUserShouldBeFound("walletPassword.notEquals=" + UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletPasswordIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletPassword in DEFAULT_WALLET_PASSWORD or UPDATED_WALLET_PASSWORD
        defaultAppUserShouldBeFound("walletPassword.in=" + DEFAULT_WALLET_PASSWORD + "," + UPDATED_WALLET_PASSWORD);

        // Get all the appUserList where walletPassword equals to UPDATED_WALLET_PASSWORD
        defaultAppUserShouldNotBeFound("walletPassword.in=" + UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletPasswordIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletPassword is not null
        defaultAppUserShouldBeFound("walletPassword.specified=true");

        // Get all the appUserList where walletPassword is null
        defaultAppUserShouldNotBeFound("walletPassword.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletPasswordContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletPassword contains DEFAULT_WALLET_PASSWORD
        defaultAppUserShouldBeFound("walletPassword.contains=" + DEFAULT_WALLET_PASSWORD);

        // Get all the appUserList where walletPassword contains UPDATED_WALLET_PASSWORD
        defaultAppUserShouldNotBeFound("walletPassword.contains=" + UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void getAllAppUsersByWalletPasswordNotContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where walletPassword does not contain DEFAULT_WALLET_PASSWORD
        defaultAppUserShouldNotBeFound("walletPassword.doesNotContain=" + DEFAULT_WALLET_PASSWORD);

        // Get all the appUserList where walletPassword does not contain UPDATED_WALLET_PASSWORD
        defaultAppUserShouldBeFound("walletPassword.doesNotContain=" + UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void getAllAppUsersByAppUserIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);
        User appUser;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            appUser = UserResourceIT.createEntity(em);
            em.persist(appUser);
            em.flush();
        } else {
            appUser = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(appUser);
        em.flush();
        appUser.setAppUser(appUser);
        appUserRepository.saveAndFlush(appUser);
        Long appUserId = appUser.getId();

        // Get all the appUserList where appUser equals to appUserId
        defaultAppUserShouldBeFound("appUserId.equals=" + appUserId);

        // Get all the appUserList where appUser equals to (appUserId + 1)
        defaultAppUserShouldNotBeFound("appUserId.equals=" + (appUserId + 1));
    }

    @Test
    @Transactional
    void getAllAppUsersByCompletedChallengesIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);
        Challenge completedChallenges;
        if (TestUtil.findAll(em, Challenge.class).isEmpty()) {
            completedChallenges = ChallengeResourceIT.createEntity(em);
            em.persist(completedChallenges);
            em.flush();
        } else {
            completedChallenges = TestUtil.findAll(em, Challenge.class).get(0);
        }
        em.persist(completedChallenges);
        em.flush();
        appUser.addCompletedChallenges(completedChallenges);
        appUserRepository.saveAndFlush(appUser);
        Long completedChallengesId = completedChallenges.getId();

        // Get all the appUserList where completedChallenges equals to completedChallengesId
        defaultAppUserShouldBeFound("completedChallengesId.equals=" + completedChallengesId);

        // Get all the appUserList where completedChallenges equals to (completedChallengesId + 1)
        defaultAppUserShouldNotBeFound("completedChallengesId.equals=" + (completedChallengesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppUserShouldBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].walletAddress").value(hasItem(DEFAULT_WALLET_ADDRESS)))
            .andExpect(jsonPath("$.[*].walletPassword").value(hasItem(DEFAULT_WALLET_PASSWORD)));

        // Check, that the count call also returns 1
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppUserShouldNotBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppUser() throws Exception {
        // Get the appUser
        restAppUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).get();
        // Disconnect from session so that the updates on updatedAppUser are not directly saved in db
        em.detach(updatedAppUser);
        updatedAppUser.walletAddress(UPDATED_WALLET_ADDRESS).walletPassword(UPDATED_WALLET_PASSWORD);

        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAppUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getWalletAddress()).isEqualTo(UPDATED_WALLET_ADDRESS);
        assertThat(testAppUser.getWalletPassword()).isEqualTo(UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void putNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser.walletAddress(UPDATED_WALLET_ADDRESS).walletPassword(UPDATED_WALLET_PASSWORD);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getWalletAddress()).isEqualTo(UPDATED_WALLET_ADDRESS);
        assertThat(testAppUser.getWalletPassword()).isEqualTo(UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser.walletAddress(UPDATED_WALLET_ADDRESS).walletPassword(UPDATED_WALLET_PASSWORD);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getWalletAddress()).isEqualTo(UPDATED_WALLET_ADDRESS);
        assertThat(testAppUser.getWalletPassword()).isEqualTo(UPDATED_WALLET_PASSWORD);
    }

    @Test
    @Transactional
    void patchNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(appUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeDelete = appUserRepository.findAll().size();

        // Delete the appUser
        restAppUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, appUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
