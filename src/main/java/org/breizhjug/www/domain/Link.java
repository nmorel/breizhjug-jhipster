package org.breizhjug.www.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Link.
 */
@Entity
@Table(name = "T_LINK")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Link implements Serializable, Comparable<Link> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @URL
    @NotNull
    @Size(max = 255)
    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;

    public Link() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Link link = (Link) o;

        return id != null && id.equals(link.id);
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
        return "Link{" +
            "id=" + id +
            ", type='" + type + "'" +
            ", url='" + url + "'" +
            ", name='" + name + "'" +
            '}';
    }

    @Override
    public int compareTo(Link o) {
        return ComparisonChain.start()
            .compare(type, o == null ? null : o.getType(), Ordering.natural().nullsLast())
            .compare(name, o == null ? null : o.getName(), Ordering.natural().nullsLast())
            .result();
    }
}
