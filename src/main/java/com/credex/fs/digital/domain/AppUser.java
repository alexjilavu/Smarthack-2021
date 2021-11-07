package com.credex.fs.digital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A AppUser.
 */
@Entity
@Table(name = "app_user")
public class AppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "wallet_password")
    private String walletPassword;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
        name = "rel_app_user__completed_challenges",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "completed_challenges_id")
    )
    @JsonIgnoreProperties(value = { "icon", "hashTags", "usersThatCompleteds" }, allowSetters = true)
    private Set<Challenge> completedChallenges = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_app_user__completed_rewards",
        joinColumns = @JoinColumn(name = "app_user_id"),
        inverseJoinColumns = @JoinColumn(name = "completed_rewards_id")
    )
    @JsonIgnoreProperties(value = { "icon", "company", "usersThatCompleteds" }, allowSetters = true)
    private Set<Reward> completedRewards = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Set<Reward> getCompletedRewards() {
        return completedRewards;
    }

    public void setCompletedRewards(Set<Reward> completedRewards) {
        this.completedRewards = completedRewards;
    }

    public AppUser addCompletedRewards(Reward reward) {
        this.completedRewards.add(reward);
        reward.getUsersThatCompleteds().add(this);
        return this;
    }

    public AppUser removeCompletedRewards(Reward reward) {
        this.completedRewards.remove(reward);
        reward.getUsersThatCompleteds().remove(this);
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public AppUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalletAddress() {
        return this.walletAddress;
    }

    public AppUser walletAddress(String walletAddress) {
        this.setWalletAddress(walletAddress);
        return this;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletPassword() {
        return this.walletPassword;
    }

    public AppUser walletPassword(String walletPassword) {
        this.setWalletPassword(walletPassword);
        return this;
    }

    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AppUser user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Challenge> getCompletedChallenges() {
        return this.completedChallenges;
    }

    public void setCompletedChallenges(Set<Challenge> challenges) {
        this.completedChallenges = challenges;
    }

    public AppUser completedChallenges(Set<Challenge> challenges) {
        this.setCompletedChallenges(challenges);
        return this;
    }

    public AppUser addCompletedChallenges(Challenge challenge) {
        this.completedChallenges.add(challenge);
        challenge.getUsersThatCompleteds().add(this);
        return this;
    }

    public AppUser removeCompletedChallenges(Challenge challenge) {
        this.completedChallenges.remove(challenge);
        challenge.getUsersThatCompleteds().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return id != null && id.equals(((AppUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", walletAddress='" + getWalletAddress() + "'" +
            ", walletPassword='" + getWalletPassword() + "'" +
            "}";
    }
}
