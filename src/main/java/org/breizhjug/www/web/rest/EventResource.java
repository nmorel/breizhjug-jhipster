package org.breizhjug.www.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.breizhjug.www.domain.Event;
import org.breizhjug.www.repository.EventRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Event.
 */
@RestController
@RequestMapping("/app")
public class EventResource {

    private final Logger log = LoggerFactory.getLogger(EventResource.class);

    @Inject
    private EventRepository eventRepository;

    /**
     * POST  /rest/events -> Create a new event.
     */
    @RequestMapping(value = "/rest/events",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Event event) {
        log.debug("REST request to save Event : {}", event);
        eventRepository.save(event);
    }

    /**
     * GET  /rest/events -> get all the events.
     */
    @RequestMapping(value = "/rest/events",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Event> getAll() {
        log.debug("REST request to get all Events");
        return eventRepository.findAll();
    }

    /**
     * GET  /rest/events/:id -> get the "id" event.
     */
    @RequestMapping(value = "/rest/events/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<Event> get(@PathVariable Long id) {
        log.debug("REST request to get Event : {}", id);
        return Optional.ofNullable(eventRepository.findOne(id))
            .map(event -> {
                // Load the lazy collection
                Hibernate.initialize(event.getSpeakers());
                Hibernate.initialize(event.getLinks());

                return new ResponseEntity<>(
                    event,
                    HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/events/:id -> delete the "id" event.
     */
    @RequestMapping(value = "/rest/events/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Event : {}", id);
        eventRepository.delete(id);
    }
}
