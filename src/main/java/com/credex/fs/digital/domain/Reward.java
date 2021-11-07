package com.credex.fs.digital.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Reward.
 */
@Entity
@Table(name = "reward")
public class Reward implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private Long value;

    @Column(name = "content")
    private String content;

    @ManyToOne
    private Icon icon;

    @ManyToOne
    @JsonIgnoreProperties(value = { "rewards" }, allowSetters = true)
    private Company company;

    @ManyToMany(mappedBy = "completedRewards")
    @JsonIgnoreProperties(value = { "appUser", "completedChallenges" }, allowSetters = true)
    private Set<AppUser> usersThatCompleteds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Set<AppUser> getUsersThatCompleteds() {
        return usersThatCompleteds;
    }

    public void setUsersThatCompleteds(Set<AppUser> usersThatCompleteds) {
        this.usersThatCompleteds = usersThatCompleteds;
    }

    public Reward addUsersThatCompleteds(AppUser appUser) {
        this.usersThatCompleteds.add(appUser);
        appUser.getCompletedRewards().add(this);
        return this;
    }

    public Reward removeUsersThatCompleteds(AppUser appUser) {
        this.usersThatCompleteds.remove(appUser);
        appUser.getCompletedRewards().remove(this);
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public Reward id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getValue() {
        return this.value;
    }

    public Reward value(Long value) {
        this.setValue(value);
        return this;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getContent() {
        return this.content;
    }

    public Reward content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Reward icon(Icon icon) {
        this.setIcon(icon);
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Reward company(Company company) {
        this.setCompany(company);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reward)) {
            return false;
        }
        return id != null && id.equals(((Reward) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reward{" +
            "id=" + getId() +
            ", value=" + getValue() +
            ", content='" + getContent() + "'" +
            "}";
    }
}
