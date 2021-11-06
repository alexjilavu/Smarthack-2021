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
 * Criteria class for the {@link com.credex.fs.digital.domain.AppUser} entity. This class is used
 * in {@link com.credex.fs.digital.web.rest.AppUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /app-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AppUserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter walletAddress;

    private StringFilter walletPassword;

    private LongFilter appUserId;

    private LongFilter completedChallengesId;

    private Boolean distinct;

    public AppUserCriteria() {}

    public AppUserCriteria(AppUserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.walletAddress = other.walletAddress == null ? null : other.walletAddress.copy();
        this.walletPassword = other.walletPassword == null ? null : other.walletPassword.copy();
        this.appUserId = other.appUserId == null ? null : other.appUserId.copy();
        this.completedChallengesId = other.completedChallengesId == null ? null : other.completedChallengesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AppUserCriteria copy() {
        return new AppUserCriteria(this);
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

    public StringFilter getWalletAddress() {
        return walletAddress;
    }

    public StringFilter walletAddress() {
        if (walletAddress == null) {
            walletAddress = new StringFilter();
        }
        return walletAddress;
    }

    public void setWalletAddress(StringFilter walletAddress) {
        this.walletAddress = walletAddress;
    }

    public StringFilter getWalletPassword() {
        return walletPassword;
    }

    public StringFilter walletPassword() {
        if (walletPassword == null) {
            walletPassword = new StringFilter();
        }
        return walletPassword;
    }

    public void setWalletPassword(StringFilter walletPassword) {
        this.walletPassword = walletPassword;
    }

    public LongFilter getAppUserId() {
        return appUserId;
    }

    public LongFilter appUserId() {
        if (appUserId == null) {
            appUserId = new LongFilter();
        }
        return appUserId;
    }

    public void setAppUserId(LongFilter appUserId) {
        this.appUserId = appUserId;
    }

    public LongFilter getCompletedChallengesId() {
        return completedChallengesId;
    }

    public LongFilter completedChallengesId() {
        if (completedChallengesId == null) {
            completedChallengesId = new LongFilter();
        }
        return completedChallengesId;
    }

    public void setCompletedChallengesId(LongFilter completedChallengesId) {
        this.completedChallengesId = completedChallengesId;
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
        final AppUserCriteria that = (AppUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(walletAddress, that.walletAddress) &&
            Objects.equals(walletPassword, that.walletPassword) &&
            Objects.equals(appUserId, that.appUserId) &&
            Objects.equals(completedChallengesId, that.completedChallengesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, walletAddress, walletPassword, appUserId, completedChallengesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (walletAddress != null ? "walletAddress=" + walletAddress + ", " : "") +
            (walletPassword != null ? "walletPassword=" + walletPassword + ", " : "") +
            (appUserId != null ? "appUserId=" + appUserId + ", " : "") +
            (completedChallengesId != null ? "completedChallengesId=" + completedChallengesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
