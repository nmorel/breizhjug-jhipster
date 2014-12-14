package org.breizhjug.www.repository;

import org.breizhjug.www.domain.Link;
import org.breizhjug.www.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Link entity.
 */
public interface LinkRepository extends JpaRepository<Link, Long> {

}
