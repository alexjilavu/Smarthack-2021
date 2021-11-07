package com.hackaton.smarthack.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hackaton.smarthack.IntegrationTest;
import com.hackaton.smarthack.domain.Transaction;
import com.hackaton.smarthack.repository.TransactionRepository;
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
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_SENDER_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_ADDRESS = "BBBBBBBBBB";

    private static final Long DEFAULT_AMOUNT = 1L;
    private static final Long UPDATED_AMOUNT = 2L;
    private static final Long SMALLER_AMOUNT = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .senderAddress(DEFAULT_SENDER_ADDRESS)
            .receiverAddress(DEFAULT_RECEIVER_ADDRESS)
            .amount(DEFAULT_AMOUNT);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .senderAddress(UPDATED_SENDER_ADDRESS)
            .receiverAddress(UPDATED_RECEIVER_ADDRESS)
            .amount(UPDATED_AMOUNT);
        return transaction;
    }

    @BeforeEach
    public void initTest() {
        transaction = createEntity(em);
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        // Create the Transaction
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getSenderAddress()).isEqualTo(DEFAULT_SENDER_ADDRESS);
        assertThat(testTransaction.getReceiverAddress()).isEqualTo(DEFAULT_RECEIVER_ADDRESS);
        assertThat(testTransaction.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].senderAddress").value(hasItem(DEFAULT_SENDER_ADDRESS)))
            .andExpect(jsonPath("$.[*].receiverAddress").value(hasItem(DEFAULT_RECEIVER_ADDRESS)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.senderAddress").value(DEFAULT_SENDER_ADDRESS))
            .andExpect(jsonPath("$.receiverAddress").value(DEFAULT_RECEIVER_ADDRESS))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionShouldBeFound("id.equals=" + id);
        defaultTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderAddress equals to DEFAULT_SENDER_ADDRESS
        defaultTransactionShouldBeFound("senderAddress.equals=" + DEFAULT_SENDER_ADDRESS);

        // Get all the transactionList where senderAddress equals to UPDATED_SENDER_ADDRESS
        defaultTransactionShouldNotBeFound("senderAddress.equals=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderAddress not equals to DEFAULT_SENDER_ADDRESS
        defaultTransactionShouldNotBeFound("senderAddress.notEquals=" + DEFAULT_SENDER_ADDRESS);

        // Get all the transactionList where senderAddress not equals to UPDATED_SENDER_ADDRESS
        defaultTransactionShouldBeFound("senderAddress.notEquals=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderAddressIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderAddress in DEFAULT_SENDER_ADDRESS or UPDATED_SENDER_ADDRESS
        defaultTransactionShouldBeFound("senderAddress.in=" + DEFAULT_SENDER_ADDRESS + "," + UPDATED_SENDER_ADDRESS);

        // Get all the transactionList where senderAddress equals to UPDATED_SENDER_ADDRESS
        defaultTransactionShouldNotBeFound("senderAddress.in=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderAddress is not null
        defaultTransactionShouldBeFound("senderAddress.specified=true");

        // Get all the transactionList where senderAddress is null
        defaultTransactionShouldNotBeFound("senderAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderAddressContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderAddress contains DEFAULT_SENDER_ADDRESS
        defaultTransactionShouldBeFound("senderAddress.contains=" + DEFAULT_SENDER_ADDRESS);

        // Get all the transactionList where senderAddress contains UPDATED_SENDER_ADDRESS
        defaultTransactionShouldNotBeFound("senderAddress.contains=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderAddressNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderAddress does not contain DEFAULT_SENDER_ADDRESS
        defaultTransactionShouldNotBeFound("senderAddress.doesNotContain=" + DEFAULT_SENDER_ADDRESS);

        // Get all the transactionList where senderAddress does not contain UPDATED_SENDER_ADDRESS
        defaultTransactionShouldBeFound("senderAddress.doesNotContain=" + UPDATED_SENDER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverAddress equals to DEFAULT_RECEIVER_ADDRESS
        defaultTransactionShouldBeFound("receiverAddress.equals=" + DEFAULT_RECEIVER_ADDRESS);

        // Get all the transactionList where receiverAddress equals to UPDATED_RECEIVER_ADDRESS
        defaultTransactionShouldNotBeFound("receiverAddress.equals=" + UPDATED_RECEIVER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverAddressIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverAddress not equals to DEFAULT_RECEIVER_ADDRESS
        defaultTransactionShouldNotBeFound("receiverAddress.notEquals=" + DEFAULT_RECEIVER_ADDRESS);

        // Get all the transactionList where receiverAddress not equals to UPDATED_RECEIVER_ADDRESS
        defaultTransactionShouldBeFound("receiverAddress.notEquals=" + UPDATED_RECEIVER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverAddressIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverAddress in DEFAULT_RECEIVER_ADDRESS or UPDATED_RECEIVER_ADDRESS
        defaultTransactionShouldBeFound("receiverAddress.in=" + DEFAULT_RECEIVER_ADDRESS + "," + UPDATED_RECEIVER_ADDRESS);

        // Get all the transactionList where receiverAddress equals to UPDATED_RECEIVER_ADDRESS
        defaultTransactionShouldNotBeFound("receiverAddress.in=" + UPDATED_RECEIVER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverAddress is not null
        defaultTransactionShouldBeFound("receiverAddress.specified=true");

        // Get all the transactionList where receiverAddress is null
        defaultTransactionShouldNotBeFound("receiverAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverAddressContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverAddress contains DEFAULT_RECEIVER_ADDRESS
        defaultTransactionShouldBeFound("receiverAddress.contains=" + DEFAULT_RECEIVER_ADDRESS);

        // Get all the transactionList where receiverAddress contains UPDATED_RECEIVER_ADDRESS
        defaultTransactionShouldNotBeFound("receiverAddress.contains=" + UPDATED_RECEIVER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverAddressNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverAddress does not contain DEFAULT_RECEIVER_ADDRESS
        defaultTransactionShouldNotBeFound("receiverAddress.doesNotContain=" + DEFAULT_RECEIVER_ADDRESS);

        // Get all the transactionList where receiverAddress does not contain UPDATED_RECEIVER_ADDRESS
        defaultTransactionShouldBeFound("receiverAddress.doesNotContain=" + UPDATED_RECEIVER_ADDRESS);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount equals to DEFAULT_AMOUNT
        defaultTransactionShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the transactionList where amount equals to UPDATED_AMOUNT
        defaultTransactionShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount not equals to DEFAULT_AMOUNT
        defaultTransactionShouldNotBeFound("amount.notEquals=" + DEFAULT_AMOUNT);

        // Get all the transactionList where amount not equals to UPDATED_AMOUNT
        defaultTransactionShouldBeFound("amount.notEquals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultTransactionShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the transactionList where amount equals to UPDATED_AMOUNT
        defaultTransactionShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is not null
        defaultTransactionShouldBeFound("amount.specified=true");

        // Get all the transactionList where amount is null
        defaultTransactionShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultTransactionShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the transactionList where amount is greater than or equal to UPDATED_AMOUNT
        defaultTransactionShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than or equal to DEFAULT_AMOUNT
        defaultTransactionShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the transactionList where amount is less than or equal to SMALLER_AMOUNT
        defaultTransactionShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than DEFAULT_AMOUNT
        defaultTransactionShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the transactionList where amount is less than UPDATED_AMOUNT
        defaultTransactionShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than DEFAULT_AMOUNT
        defaultTransactionShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the transactionList where amount is greater than SMALLER_AMOUNT
        defaultTransactionShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].senderAddress").value(hasItem(DEFAULT_SENDER_ADDRESS)))
            .andExpect(jsonPath("$.[*].receiverAddress").value(hasItem(DEFAULT_RECEIVER_ADDRESS)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction.senderAddress(UPDATED_SENDER_ADDRESS).receiverAddress(UPDATED_RECEIVER_ADDRESS).amount(UPDATED_AMOUNT);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getSenderAddress()).isEqualTo(UPDATED_SENDER_ADDRESS);
        assertThat(testTransaction.getReceiverAddress()).isEqualTo(UPDATED_RECEIVER_ADDRESS);
        assertThat(testTransaction.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.receiverAddress(UPDATED_RECEIVER_ADDRESS);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getSenderAddress()).isEqualTo(DEFAULT_SENDER_ADDRESS);
        assertThat(testTransaction.getReceiverAddress()).isEqualTo(UPDATED_RECEIVER_ADDRESS);
        assertThat(testTransaction.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.senderAddress(UPDATED_SENDER_ADDRESS).receiverAddress(UPDATED_RECEIVER_ADDRESS).amount(UPDATED_AMOUNT);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getSenderAddress()).isEqualTo(UPDATED_SENDER_ADDRESS);
        assertThat(testTransaction.getReceiverAddress()).isEqualTo(UPDATED_RECEIVER_ADDRESS);
        assertThat(testTransaction.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
