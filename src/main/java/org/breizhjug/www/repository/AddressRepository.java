package org.breizhjug.www.repository;

import org.breizhjug.www.domain.Address;
import org.breizhjug.www.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Address entity.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

}
