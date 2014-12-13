package org.breizhjug.www.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Address.
 */
@Entity
@Table(name = "T_ADDRESS")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Address implements Serializable, Comparable<Address> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private EnumTypeAddress type;

    @NotNull
    @Size(max = 255)
    @Column(name = "value", nullable = false, unique = true, length = 255)
    private String value;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonBackReference
    private Person person;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumTypeAddress getType() {
        return type;
    }

    public void setType(EnumTypeAddress type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;

        return id != null && id.equals(address.id);
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
        return "Address{" +
            "id=" + id +
            ", type='" + type + "'" +
            ", value='" + value + "'" +
            '}';
    }

    @Override
    public int compareTo(Address o) {
        return ComparisonChain.start()
            .compare(type.name(), o == null ? null : o.getType().name(), Ordering.natural().nullsLast())
            .compare(value, o == null ? null : o.getValue(), Ordering.natural().nullsLast())
            .result();
    }
}
