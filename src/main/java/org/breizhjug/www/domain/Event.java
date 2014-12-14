package org.breizhjug.www.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.breizhjug.www.domain.util.CustomDateTimeDeserializer;
import org.breizhjug.www.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Event.
 */
@Entity
@Table(name = "T_EVENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name")
    private String name;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "date", nullable = false)
    private DateTime date;

    @NotNull
    @Size(max = 100)
    @Column(name = "place")
    private String place;

    @Column(name = "image")
    @Lob
    private String image;

    @Size(max = 2000)
    @Column(name = "resume", length = 2000, nullable = false)
    private String resume;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "T_EVENT_T_PERSON", joinColumns = {
        @JoinColumn(name = "event_id", nullable = false, updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "person_id",
            nullable = false, updatable = false)})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Person> speakers = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonManagedReference
    private Set<Link> links = new HashSet<>();

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

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Set<Person> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<Person> speakers) {
        this.speakers = speakers;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Event event = (Event) o;

        return id != null && id.equals(event.id);
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
        return "Event{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", date='" + date + "'" +
            ", place='" + place + "'" +
            ", resume='" + resume + "'" +
            '}';
    }
}
