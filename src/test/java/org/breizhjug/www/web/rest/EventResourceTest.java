package org.breizhjug.www.web.rest;

import org.breizhjug.www.Application;
import org.breizhjug.www.domain.Event;
import org.breizhjug.www.domain.Link;
import org.breizhjug.www.domain.Person;
import org.breizhjug.www.repository.EventRepository;
import org.breizhjug.www.repository.LinkRepository;
import org.breizhjug.www.repository.PersonRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EventResource REST controller.
 *
 * @see EventResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EventResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Inject
    private PersonRepository personRepository;

    @Inject
    private LinkRepository linkRepository;

    @Inject
    private EventRepository eventRepository;

    private MockMvc restEventMockMvc;

    private Event event;

    private Person speaker1;

    private Person speaker2;

    private Link link1;

    private Link link2;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EventResource eventResource = new EventResource();
        ReflectionTestUtils.setField(eventResource, "eventRepository", eventRepository);
        this.restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource).build();
    }

    @Before
    public void initTest() {
        speaker1 = new Person();
        speaker1.setName("Speaker1");
        speaker1.setSpeaker(true);

        speaker2 = new Person();
        speaker2.setName("Speaker2");
        speaker2.setSpeaker(true);

        event = new Event();
        event.setName("My event");
        event.setDate(new DateTime(2014, 8, 12, 18, 0, 0, 0));
        event.setPlace("IFSIC");
        event.setImage("base64imageeeeeeeeeeeeeeeeeeee");
        event.setResume("A very short resume");
        event.setSpeakers(new HashSet<>(Arrays.asList(speaker1, speaker2)));

        link1 = new Link();
        link1.setName("Link1");
        link1.setType("video");
        link1.setUrl("http://www.link1.com");
        link1.setEvent(event);

        link2 = new Link();
        link2.setName("Link2");
        link2.setType("slides");
        link2.setUrl("http://www.link2.com");
        link2.setEvent(event);

        event.setLinks(new HashSet<>(Arrays.asList(link1, link2)));
    }

    @Test
    @Transactional
    public void createEvent() throws Exception {
        // Validate the database is empty
        assertThat(eventRepository.findAll()).hasSize(0);

        personRepository.saveAndFlush(speaker1);
        personRepository.saveAndFlush(speaker2);

        // Create the Event
        restEventMockMvc.perform(post("/app/rest/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(1);
        Event testEvent = events.iterator().next();
        assertThat(testEvent.getName()).isEqualTo(event.getName());
        assertThat(testEvent.getDate()).isEqualTo(event.getDate());
        assertThat(testEvent.getPlace()).isEqualTo(event.getPlace());
        assertThat(testEvent.getImage()).isEqualTo(event.getImage());
        assertThat(testEvent.getResume()).isEqualTo(event.getResume());

        assertThat(testEvent.getSpeakers()).hasSize(2);
        List<Person> testSpeakers = new ArrayList<>(testEvent.getSpeakers());
        Collections.sort(testSpeakers);
        assertThat(testSpeakers.get(0)).isEqualTo(speaker1);
        assertThat(testSpeakers.get(1)).isEqualTo(speaker2);

        assertThat(testEvent.getLinks()).hasSize(2);
        List<Link> testLinks = new ArrayList<>(testEvent.getLinks());
        Collections.sort(testLinks);

        Link testLink2 = testLinks.get(0);
        assertThat(testLink2.getType()).isEqualTo(link2.getType());
        assertThat(testLink2.getName()).isEqualTo(link2.getName());
        assertThat(testLink2.getUrl()).isEqualTo(link2.getUrl());

        Link testLink1 = testLinks.get(1);
        assertThat(testLink1.getType()).isEqualTo(link1.getType());
        assertThat(testLink1.getName()).isEqualTo(link1.getName());
        assertThat(testLink1.getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    @Transactional
    public void getAllEvents() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(speaker1);
        personRepository.saveAndFlush(speaker2);
        eventRepository.saveAndFlush(event);

        // Get all the events
        restEventMockMvc.perform(get("/app/rest/events"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.[0].name").value(event.getName()))
            .andExpect(jsonPath("$.[0].date").value(dateTimeFormatter.print(event.getDate())))
            .andExpect(jsonPath("$.[0].place").value(event.getPlace()))
            .andExpect(jsonPath("$.[0].image").value(event.getImage()))
            .andExpect(jsonPath("$.[0].resume").value(event.getResume()))
            .andExpect(jsonPath("$.[0].speakers[?(@.name == 'Speaker1')]").exists())
            .andExpect(jsonPath("$.[0].speakers[?(@.name == 'Speaker2'))]").exists())
            .andExpect(jsonPath("$.[0].links[?(@.type == 'video' && @.name == 'Link1' && @.url == 'http://www.link1.com')]").exists())
            .andExpect(jsonPath("$.[0].links[?(@.type == 'slides' && @.name == 'Link2' && @.url == 'http://www.link2.com')]").exists());
    }

    @Test
    @Transactional
    public void getEvent() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(speaker1);
        personRepository.saveAndFlush(speaker2);
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc.perform(get("/app/rest/events/{id}", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.name").value(event.getName()))
            .andExpect(jsonPath("$.date").value(dateTimeFormatter.print(event.getDate())))
            .andExpect(jsonPath("$.place").value(event.getPlace()))
            .andExpect(jsonPath("$.image").value(event.getImage()))
            .andExpect(jsonPath("$.resume").value(event.getResume()))
            .andExpect(jsonPath("$.speakers[?(@.name == 'Speaker1')]").exists())
            .andExpect(jsonPath("$.speakers[?(@.name == 'Speaker2'))]").exists())
            .andExpect(jsonPath("$.links[?(@.type == 'video' && @.name == 'Link1' && @.url == 'http://www.link1.com')]").exists())
            .andExpect(jsonPath("$.links[?(@.type == 'slides' && @.name == 'Link2' && @.url == 'http://www.link2.com')]").exists());
    }

    @Test
    @Transactional
    public void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/app/rest/events/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEvent() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(speaker1);
        personRepository.saveAndFlush(speaker2);
        eventRepository.saveAndFlush(event);

        // Update the event
        event.setName("My updated event");

        link1.setName("Link1 updated");

        Link link3 = new Link();
        link3.setName("Link3");
        link3.setType("slides");
        link3.setUrl("http://www.link3.com");
        link3.setEvent(event);

        event.getLinks().clear();
        event.getLinks().add(link1);
        event.getLinks().add(link3);

        event.getSpeakers().clear();
        event.getSpeakers().add(speaker1);

        restEventMockMvc.perform(post("/app/rest/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(1);
        Event testEvent = events.iterator().next();
        assertThat(testEvent.getName()).isEqualTo(event.getName());
        assertThat(testEvent.getDate()).isEqualTo(event.getDate());
        assertThat(testEvent.getPlace()).isEqualTo(event.getPlace());
        assertThat(testEvent.getImage()).isEqualTo(event.getImage());
        assertThat(testEvent.getResume()).isEqualTo(event.getResume());

        assertThat(testEvent.getSpeakers()).hasSize(1);
        assertThat(testEvent.getSpeakers().iterator().next()).isEqualTo(speaker1);

        assertThat(testEvent.getLinks()).hasSize(2);
        List<Link> testLinks = new ArrayList<>(testEvent.getLinks());
        Collections.sort(testLinks);

        Link testLink3 = testLinks.get(0);
        assertThat(testLink3.getType()).isEqualTo(link3.getType());
        assertThat(testLink3.getName()).isEqualTo(link3.getName());
        assertThat(testLink3.getUrl()).isEqualTo(link3.getUrl());

        Link testLink1 = testLinks.get(1);
        assertThat(testLink1.getType()).isEqualTo(link1.getType());
        assertThat(testLink1.getName()).isEqualTo(link1.getName());
        assertThat(testLink1.getUrl()).isEqualTo(link1.getUrl());
    }

    @Test
    @Transactional
    public void deleteEvent() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(speaker1);
        personRepository.saveAndFlush(speaker2);
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc.perform(delete("/app/rest/events/{id}", event.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(0);
        List<Link> links = linkRepository.findAll();
        assertThat(links).hasSize(0);

        // speakers should not be deleted
        List<Person> persons = personRepository.findAll();
        assertThat(persons).hasSize(2);
    }
}
