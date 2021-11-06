package com.credex.fs.digital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Challenge.
 */
@Entity
@Table(name = "challenge")
public class Challenge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "reward_amount")
    private Long rewardAmount;

    @Column(name = "required_tags")
    private String requiredTags;

    @ManyToOne
    private Icon icon;

    @ManyToMany
    @JoinTable(
        name = "rel_challenge__hash_tags",
        joinColumns = @JoinColumn(name = "challenge_id"),
        inverseJoinColumns = @JoinColumn(name = "hash_tags_id")
    )
    @JsonIgnoreProperties(value = { "challenges" }, allowSetters = true)
    private Set<HashTag> hashTags = new HashSet<>();

    @ManyToMany(mappedBy = "completedChallenges")
    @JsonIgnoreProperties(value = { "appUser", "completedChallenges" }, allowSetters = true)
    private Set<AppUser> usersThatCompleteds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getRequiredTags() {
        return requiredTags;
    }

    public void setRequiredTags(String requiredTags) {
        this.requiredTags = requiredTags;
    }

    public Long getId() {
        return this.id;
    }

    public Challenge id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Challenge title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public Challenge message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public Challenge iconUrl(String iconUrl) {
        this.setIconUrl(iconUrl);
        return this;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Long getRewardAmount() {
        return this.rewardAmount;
    }

    public Challenge rewardAmount(Long rewardAmount) {
        this.setRewardAmount(rewardAmount);
        return this;
    }

    public void setRewardAmount(Long rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Challenge icon(Icon icon) {
        this.setIcon(icon);
        return this;
    }

    public Set<HashTag> getHashTags() {
        return this.hashTags;
    }

    public void setHashTags(Set<HashTag> hashTags) {
        this.hashTags = hashTags;
    }

    public Challenge hashTags(Set<HashTag> hashTags) {
        this.setHashTags(hashTags);
        return this;
    }

    public Challenge addHashTags(HashTag hashTag) {
        this.hashTags.add(hashTag);
        hashTag.getChallenges().add(this);
        return this;
    }

    public Challenge removeHashTags(HashTag hashTag) {
        this.hashTags.remove(hashTag);
        hashTag.getChallenges().remove(this);
        return this;
    }

    public Set<AppUser> getUsersThatCompleteds() {
        return this.usersThatCompleteds;
    }

    public void setUsersThatCompleteds(Set<AppUser> appUsers) {
        if (this.usersThatCompleteds != null) {
            this.usersThatCompleteds.forEach(i -> i.removeCompletedChallenges(this));
        }
        if (appUsers != null) {
            appUsers.forEach(i -> i.addCompletedChallenges(this));
        }
        this.usersThatCompleteds = appUsers;
    }

    public Challenge usersThatCompleteds(Set<AppUser> appUsers) {
        this.setUsersThatCompleteds(appUsers);
        return this;
    }

    public Challenge addUsersThatCompleted(AppUser appUser) {
        this.usersThatCompleteds.add(appUser);
        appUser.getCompletedChallenges().add(this);
        return this;
    }

    public Challenge removeUsersThatCompleted(AppUser appUser) {
        this.usersThatCompleteds.remove(appUser);
        appUser.getCompletedChallenges().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Challenge)) {
            return false;
        }
        return id != null && id.equals(((Challenge) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Challenge{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", message='" + getMessage() + "'" +
            ", iconUrl='" + getIconUrl() + "'" +
            ", rewardAmount=" + getRewardAmount() +
            "}";
    }
}
