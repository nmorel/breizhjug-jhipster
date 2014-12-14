package org.breizhjug.www.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Person.
 */
@Entity
@Table(name = "T_PERSON")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Person implements Serializable, Comparable<Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "photo")
    @Lob
    private String photo;

    @Size(max = 2000)
    @Column(name = "resume", length = 2000)
    private String resume;

    @Column(name = "speaker", nullable = false)
    private boolean speaker;

    @Column(name = "team_member", nullable = false)
    private boolean teamMember;

    @OneToMany(mappedBy = "person", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonManagedReference
    private Set<Address> addresses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public boolean isSpeaker() {
        return speaker;
    }

    public void setSpeaker(boolean speaker) {
        this.speaker = speaker;
    }

    public boolean isTeamMember() {
        return teamMember;
    }

    public void setTeamMember(boolean teamMember) {
        this.teamMember = teamMember;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Person person = (Person) o;

        return id != null && id.equals(person.id);
    }

    @Override
    public int hashCode() {
        if (null == id) {
            return super.hashCode();
        }
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Person{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", resume='" + resume + "'" +
            ", speaker='" + speaker + "'" +
            ", team_member='" + teamMember + "'" +
            '}';
    }

    @Override
    public int compareTo(Person o) {
        return ComparisonChain.start()
            .compare(name, o == null ? null : o.getName(), Ordering.natural().nullsLast())
            .result();
    }
}
