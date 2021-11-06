package com.credex.fs.digital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A HashTag.
 */
@Entity
@Table(name = "hash_tag")
public class HashTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "company")
    private String company;

    @ManyToMany(mappedBy = "hashTags")
    @JsonIgnoreProperties(value = { "icon", "hashTags", "usersThatCompleteds" }, allowSetters = true)
    private Set<Challenge> challenges = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HashTag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public HashTag name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return this.company;
    }

    public HashTag company(String company) {
        this.setCompany(company);
        return this;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Set<Challenge> getChallenges() {
        return this.challenges;
    }

    public void setChallenges(Set<Challenge> challenges) {
        if (this.challenges != null) {
            this.challenges.forEach(i -> i.removeHashTags(this));
        }
        if (challenges != null) {
            challenges.forEach(i -> i.addHashTags(this));
        }
        this.challenges = challenges;
    }

    public HashTag challenges(Set<Challenge> challenges) {
        this.setChallenges(challenges);
        return this;
    }

    public HashTag addChallenges(Challenge challenge) {
        this.challenges.add(challenge);
        challenge.getHashTags().add(this);
        return this;
    }

    public HashTag removeChallenges(Challenge challenge) {
        this.challenges.remove(challenge);
        challenge.getHashTags().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HashTag)) {
            return false;
        }
        return id != null && id.equals(((HashTag) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HashTag{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", company='" + getCompany() + "'" +
            "}";
    }
}
