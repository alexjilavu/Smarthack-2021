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
 * Criteria class for the {@link com.credex.fs.digital.domain.Challenge} entity. This class is used
 * in {@link com.credex.fs.digital.web.rest.ChallengeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /challenges?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ChallengeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter message;

    private StringFilter iconUrl;

    private LongFilter rewardAmount;

    private LongFilter iconId;

    private LongFilter hashTagsId;

    private LongFilter usersThatCompletedId;

    private Boolean distinct;

    public ChallengeCriteria() {}

    public ChallengeCriteria(ChallengeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.message = other.message == null ? null : other.message.copy();
        this.iconUrl = other.iconUrl == null ? null : other.iconUrl.copy();
        this.rewardAmount = other.rewardAmount == null ? null : other.rewardAmount.copy();
        this.iconId = other.iconId == null ? null : other.iconId.copy();
        this.hashTagsId = other.hashTagsId == null ? null : other.hashTagsId.copy();
        this.usersThatCompletedId = other.usersThatCompletedId == null ? null : other.usersThatCompletedId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ChallengeCriteria copy() {
        return new ChallengeCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getMessage() {
        return message;
    }

    public StringFilter message() {
        if (message == null) {
            message = new StringFilter();
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public StringFilter getIconUrl() {
        return iconUrl;
    }

    public StringFilter iconUrl() {
        if (iconUrl == null) {
            iconUrl = new StringFilter();
        }
        return iconUrl;
    }

    public void setIconUrl(StringFilter iconUrl) {
        this.iconUrl = iconUrl;
    }

    public LongFilter getRewardAmount() {
        return rewardAmount;
    }

    public LongFilter rewardAmount() {
        if (rewardAmount == null) {
            rewardAmount = new LongFilter();
        }
        return rewardAmount;
    }

    public void setRewardAmount(LongFilter rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public LongFilter getIconId() {
        return iconId;
    }

    public LongFilter iconId() {
        if (iconId == null) {
            iconId = new LongFilter();
        }
        return iconId;
    }

    public void setIconId(LongFilter iconId) {
        this.iconId = iconId;
    }

    public LongFilter getHashTagsId() {
        return hashTagsId;
    }

    public LongFilter hashTagsId() {
        if (hashTagsId == null) {
            hashTagsId = new LongFilter();
        }
        return hashTagsId;
    }

    public void setHashTagsId(LongFilter hashTagsId) {
        this.hashTagsId = hashTagsId;
    }

    public LongFilter getUsersThatCompletedId() {
        return usersThatCompletedId;
    }

    public LongFilter usersThatCompletedId() {
        if (usersThatCompletedId == null) {
            usersThatCompletedId = new LongFilter();
        }
        return usersThatCompletedId;
    }

    public void setUsersThatCompletedId(LongFilter usersThatCompletedId) {
        this.usersThatCompletedId = usersThatCompletedId;
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
        final ChallengeCriteria that = (ChallengeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(message, that.message) &&
            Objects.equals(iconUrl, that.iconUrl) &&
            Objects.equals(rewardAmount, that.rewardAmount) &&
            Objects.equals(iconId, that.iconId) &&
            Objects.equals(hashTagsId, that.hashTagsId) &&
            Objects.equals(usersThatCompletedId, that.usersThatCompletedId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, message, iconUrl, rewardAmount, iconId, hashTagsId, usersThatCompletedId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChallengeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (message != null ? "message=" + message + ", " : "") +
            (iconUrl != null ? "iconUrl=" + iconUrl + ", " : "") +
            (rewardAmount != null ? "rewardAmount=" + rewardAmount + ", " : "") +
            (iconId != null ? "iconId=" + iconId + ", " : "") +
            (hashTagsId != null ? "hashTagsId=" + hashTagsId + ", " : "") +
            (usersThatCompletedId != null ? "usersThatCompletedId=" + usersThatCompletedId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
