package com.credex.fs.digital.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.credex.fs.digital.domain.Transaction} entity. This class is used
 * in {@link com.credex.fs.digital.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TransactionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter senderAddress;

    private StringFilter receiverAddress;

    private LongFilter amount;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.senderAddress = other.senderAddress == null ? null : other.senderAddress.copy();
        this.receiverAddress = other.receiverAddress == null ? null : other.receiverAddress.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSenderAddress() {
        return senderAddress;
    }

    public StringFilter senderAddress() {
        if (senderAddress == null) {
            senderAddress = new StringFilter();
        }
        return senderAddress;
    }

    public void setSenderAddress(StringFilter senderAddress) {
        this.senderAddress = senderAddress;
    }

    public StringFilter getReceiverAddress() {
        return receiverAddress;
    }

    public StringFilter receiverAddress() {
        if (receiverAddress == null) {
            receiverAddress = new StringFilter();
        }
        return receiverAddress;
    }

    public void setReceiverAddress(StringFilter receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public LongFilter getAmount() {
        return amount;
    }

    public LongFilter amount() {
        if (amount == null) {
            amount = new LongFilter();
        }
        return amount;
    }

    public void setAmount(LongFilter amount) {
        this.amount = amount;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(senderAddress, that.senderAddress) &&
            Objects.equals(receiverAddress, that.receiverAddress) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, senderAddress, receiverAddress, amount, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (senderAddress != null ? "senderAddress=" + senderAddress + ", " : "") +
            (receiverAddress != null ? "receiverAddress=" + receiverAddress + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
