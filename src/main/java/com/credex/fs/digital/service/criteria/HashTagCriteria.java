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
 * Criteria class for the {@link com.credex.fs.digital.domain.HashTag} entity. This class is used
 * in {@link com.credex.fs.digital.web.rest.HashTagResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /hash-tags?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class HashTagCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter company;

    private LongFilter challengesId;

    private Boolean distinct;

    public HashTagCriteria() {}

    public HashTagCriteria(HashTagCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.company = other.company == null ? null : other.company.copy();
        this.challengesId = other.challengesId == null ? null : other.challengesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public HashTagCriteria copy() {
        return new HashTagCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCompany() {
        return company;
    }

    public StringFilter company() {
        if (company == null) {
            company = new StringFilter();
        }
        return company;
    }

    public void setCompany(StringFilter company) {
        this.company = company;
    }

    public LongFilter getChallengesId() {
        return challengesId;
    }

    public LongFilter challengesId() {
        if (challengesId == null) {
            challengesId = new LongFilter();
        }
        return challengesId;
    }

    public void setChallengesId(LongFilter challengesId) {
        this.challengesId = challengesId;
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
        final HashTagCriteria that = (HashTagCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(company, that.company) &&
            Objects.equals(challengesId, that.challengesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, company, challengesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HashTagCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (company != null ? "company=" + company + ", " : "") +
            (challengesId != null ? "challengesId=" + challengesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
