package org.breizhjug.www.repository;

import org.breizhjug.www.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Person entity.
 */
public interface PersonRepository extends JpaRepository<Person, Long> {

}
