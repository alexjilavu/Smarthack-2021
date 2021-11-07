package com.credex.fs.digital.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;

/**
 * A Post.
 */
@Entity
@Table(name = "post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Lob
    @Column(name = "image_url")
    private byte[] imageUrl;

    @Column(name = "image_url_content_type")
    private String imageUrlContentType;

    @Column(name = "published_by")
    private String publishedBy;

    @Column(name = "hash_tags")
    private String hashTags;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "no_of_likes")
    private Integer noOfLikes;

    @Column(name = "no_of_shares")
    private Integer noOfShares;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public Post id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Post content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImageUrl() {
        return this.imageUrl;
    }

    public Post imageUrl(byte[] imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(byte[] imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrlContentType() {
        return this.imageUrlContentType;
    }

    public Post imageUrlContentType(String imageUrlContentType) {
        this.imageUrlContentType = imageUrlContentType;
        return this;
    }

    public void setImageUrlContentType(String imageUrlContentType) {
        this.imageUrlContentType = imageUrlContentType;
    }

    public String getPublishedBy() {
        return this.publishedBy;
    }

    public Post publishedBy(String publishedBy) {
        this.setPublishedBy(publishedBy);
        return this;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public Integer getNoOfLikes() {
        return this.noOfLikes;
    }

    public Post noOfLikes(Integer noOfLikes) {
        this.setNoOfLikes(noOfLikes);
        return this;
    }

    public void setNoOfLikes(Integer noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public Integer getNoOfShares() {
        return this.noOfShares;
    }

    public Post noOfShares(Integer noOfShares) {
        this.setNoOfShares(noOfShares);
        return this;
    }

    public Post hashTags(String hashTags) {
        this.setHashTags(hashTags);
        return this;
    }

    public Post createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setNoOfShares(Integer noOfShares) {
        this.noOfShares = noOfShares;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return id != null && id.equals(((Post) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", imageUrlContentType='" + getImageUrlContentType() + "'" +
            ", publishedBy='" + getPublishedBy() + "'" +
            ", noOfLikes=" + getNoOfLikes() +
            ", noOfShares=" + getNoOfShares() +
            "}";
    }
}
