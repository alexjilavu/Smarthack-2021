package com.credex.fs.digital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Company.
 */
@Entity
@Table(name = "company")
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "company")
    @JsonIgnoreProperties(value = { "icon", "company" }, allowSetters = true)
    private Set<Reward> rewards = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Company id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Company name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Reward> getRewards() {
        return this.rewards;
    }

    public void setRewards(Set<Reward> rewards) {
        if (this.rewards != null) {
            this.rewards.forEach(i -> i.setCompany(null));
        }
        if (rewards != null) {
            rewards.forEach(i -> i.setCompany(this));
        }
        this.rewards = rewards;
    }

    public Company rewards(Set<Reward> rewards) {
        this.setRewards(rewards);
        return this;
    }

    public Company addRewards(Reward reward) {
        this.rewards.add(reward);
        reward.setCompany(this);
        return this;
    }

    public Company removeRewards(Reward reward) {
        this.rewards.remove(reward);
        reward.setCompany(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        return id != null && id.equals(((Company) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Company{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
