package org.breizhjug.www.repository;

import org.breizhjug.www.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Event entity.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

}
