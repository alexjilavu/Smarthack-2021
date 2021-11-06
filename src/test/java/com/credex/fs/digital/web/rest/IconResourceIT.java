package com.credex.fs.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.credex.fs.digital.IntegrationTest;
import com.credex.fs.digital.domain.Icon;
import com.credex.fs.digital.repository.IconRepository;
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
 * Integration tests for the {@link IconResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IconResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/icons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IconRepository iconRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIconMockMvc;

    private Icon icon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Icon createEntity(EntityManager em) {
        Icon icon = new Icon().name(DEFAULT_NAME).url(DEFAULT_URL);
        return icon;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Icon createUpdatedEntity(EntityManager em) {
        Icon icon = new Icon().name(UPDATED_NAME).url(UPDATED_URL);
        return icon;
    }

    @BeforeEach
    public void initTest() {
        icon = createEntity(em);
    }

    @Test
    @Transactional
    void createIcon() throws Exception {
        int databaseSizeBeforeCreate = iconRepository.findAll().size();
        // Create the Icon
        restIconMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(icon)))
            .andExpect(status().isCreated());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeCreate + 1);
        Icon testIcon = iconList.get(iconList.size() - 1);
        assertThat(testIcon.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIcon.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void createIconWithExistingId() throws Exception {
        // Create the Icon with an existing ID
        icon.setId(1L);

        int databaseSizeBeforeCreate = iconRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIconMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(icon)))
            .andExpect(status().isBadRequest());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllIcons() throws Exception {
        // Initialize the database
        iconRepository.saveAndFlush(icon);

        // Get all the iconList
        restIconMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(icon.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @Test
    @Transactional
    void getIcon() throws Exception {
        // Initialize the database
        iconRepository.saveAndFlush(icon);

        // Get the icon
        restIconMockMvc
            .perform(get(ENTITY_API_URL_ID, icon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(icon.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getNonExistingIcon() throws Exception {
        // Get the icon
        restIconMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewIcon() throws Exception {
        // Initialize the database
        iconRepository.saveAndFlush(icon);

        int databaseSizeBeforeUpdate = iconRepository.findAll().size();

        // Update the icon
        Icon updatedIcon = iconRepository.findById(icon.getId()).get();
        // Disconnect from session so that the updates on updatedIcon are not directly saved in db
        em.detach(updatedIcon);
        updatedIcon.name(UPDATED_NAME).url(UPDATED_URL);

        restIconMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIcon.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIcon))
            )
            .andExpect(status().isOk());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
        Icon testIcon = iconList.get(iconList.size() - 1);
        assertThat(testIcon.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIcon.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void putNonExistingIcon() throws Exception {
        int databaseSizeBeforeUpdate = iconRepository.findAll().size();
        icon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIconMockMvc
            .perform(
                put(ENTITY_API_URL_ID, icon.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(icon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIcon() throws Exception {
        int databaseSizeBeforeUpdate = iconRepository.findAll().size();
        icon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIconMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(icon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIcon() throws Exception {
        int databaseSizeBeforeUpdate = iconRepository.findAll().size();
        icon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIconMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(icon)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIconWithPatch() throws Exception {
        // Initialize the database
        iconRepository.saveAndFlush(icon);

        int databaseSizeBeforeUpdate = iconRepository.findAll().size();

        // Update the icon using partial update
        Icon partialUpdatedIcon = new Icon();
        partialUpdatedIcon.setId(icon.getId());

        restIconMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIcon.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIcon))
            )
            .andExpect(status().isOk());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
        Icon testIcon = iconList.get(iconList.size() - 1);
        assertThat(testIcon.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIcon.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void fullUpdateIconWithPatch() throws Exception {
        // Initialize the database
        iconRepository.saveAndFlush(icon);

        int databaseSizeBeforeUpdate = iconRepository.findAll().size();

        // Update the icon using partial update
        Icon partialUpdatedIcon = new Icon();
        partialUpdatedIcon.setId(icon.getId());

        partialUpdatedIcon.name(UPDATED_NAME).url(UPDATED_URL);

        restIconMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIcon.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIcon))
            )
            .andExpect(status().isOk());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
        Icon testIcon = iconList.get(iconList.size() - 1);
        assertThat(testIcon.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIcon.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingIcon() throws Exception {
        int databaseSizeBeforeUpdate = iconRepository.findAll().size();
        icon.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIconMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, icon.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(icon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIcon() throws Exception {
        int databaseSizeBeforeUpdate = iconRepository.findAll().size();
        icon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIconMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(icon))
            )
            .andExpect(status().isBadRequest());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIcon() throws Exception {
        int databaseSizeBeforeUpdate = iconRepository.findAll().size();
        icon.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIconMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(icon)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Icon in the database
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIcon() throws Exception {
        // Initialize the database
        iconRepository.saveAndFlush(icon);

        int databaseSizeBeforeDelete = iconRepository.findAll().size();

        // Delete the icon
        restIconMockMvc
            .perform(delete(ENTITY_API_URL_ID, icon.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Icon> iconList = iconRepository.findAll();
        assertThat(iconList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
