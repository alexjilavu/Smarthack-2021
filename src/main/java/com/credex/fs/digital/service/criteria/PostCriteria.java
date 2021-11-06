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
 * Criteria class for the {@link com.credex.fs.digital.domain.Post} entity. This class is used
 * in {@link com.credex.fs.digital.web.rest.PostResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PostCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private StringFilter publishedBy;

    private IntegerFilter noOfLikes;

    private IntegerFilter noOfShares;

    private Boolean distinct;

    public PostCriteria() {}

    public PostCriteria(PostCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.publishedBy = other.publishedBy == null ? null : other.publishedBy.copy();
        this.noOfLikes = other.noOfLikes == null ? null : other.noOfLikes.copy();
        this.noOfShares = other.noOfShares == null ? null : other.noOfShares.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PostCriteria copy() {
        return new PostCriteria(this);
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

    public StringFilter getContent() {
        return content;
    }

    public StringFilter content() {
        if (content == null) {
            content = new StringFilter();
        }
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public StringFilter getPublishedBy() {
        return publishedBy;
    }

    public StringFilter publishedBy() {
        if (publishedBy == null) {
            publishedBy = new StringFilter();
        }
        return publishedBy;
    }

    public void setPublishedBy(StringFilter publishedBy) {
        this.publishedBy = publishedBy;
    }

    public IntegerFilter getNoOfLikes() {
        return noOfLikes;
    }

    public IntegerFilter noOfLikes() {
        if (noOfLikes == null) {
            noOfLikes = new IntegerFilter();
        }
        return noOfLikes;
    }

    public void setNoOfLikes(IntegerFilter noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public IntegerFilter getNoOfShares() {
        return noOfShares;
    }

    public IntegerFilter noOfShares() {
        if (noOfShares == null) {
            noOfShares = new IntegerFilter();
        }
        return noOfShares;
    }

    public void setNoOfShares(IntegerFilter noOfShares) {
        this.noOfShares = noOfShares;
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
        final PostCriteria that = (PostCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(publishedBy, that.publishedBy) &&
            Objects.equals(noOfLikes, that.noOfLikes) &&
            Objects.equals(noOfShares, that.noOfShares) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, publishedBy, noOfLikes, noOfShares, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (content != null ? "content=" + content + ", " : "") +
            (publishedBy != null ? "publishedBy=" + publishedBy + ", " : "") +
            (noOfLikes != null ? "noOfLikes=" + noOfLikes + ", " : "") +
            (noOfShares != null ? "noOfShares=" + noOfShares + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
