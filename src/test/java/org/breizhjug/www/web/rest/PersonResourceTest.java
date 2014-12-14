package org.breizhjug.www.web.rest;

import org.breizhjug.www.Application;
import org.breizhjug.www.domain.Address;
import org.breizhjug.www.domain.EnumTypeAddress;
import org.breizhjug.www.domain.Person;
import org.breizhjug.www.repository.AddressRepository;
import org.breizhjug.www.repository.PersonRepository;
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
 * Test class for the PersonResource REST controller.
 *
 * @see PersonResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PersonResourceTest {

    @Inject
    private PersonRepository personRepository;

    @Inject
    private AddressRepository addressRepository;

    private MockMvc restPersonMockMvc;

    private Person person;

    private Address github;
    private Address twitter;
    private Address mail;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PersonResource personResource = new PersonResource();
        ReflectionTestUtils.setField(personResource, "personRepository", personRepository);
        this.restPersonMockMvc = MockMvcBuilders.standaloneSetup(personResource).build();
    }

    @Before
    public void initTest() {
        person = new Person();
        person.setName("John Doe");
        person.setPhoto("azernhdlkgjdfkjbhkljzerkjghdkfgndkfjbkjfhglkrdhgkjkh");
        person.setResume("John Doe is a great guy!");
        person.setSpeaker(true);
        person.setTeamMember(false);

        github = new Address();
        github.setType(EnumTypeAddress.Github);
        github.setValue("john_doe");
        github.setPerson(person);
        person.getAddresses().add(github);

        twitter = new Address();
        twitter.setType(EnumTypeAddress.Twitter);
        twitter.setValue("@johndoe");
        twitter.setPerson(person);
        person.getAddresses().add(twitter);

        mail = new Address();
        mail.setType(EnumTypeAddress.Mail);
        mail.setValue("john.doe@gmail.com");
        mail.setPerson(person);
        person.getAddresses().add(mail);
    }

    @Test
    @Transactional
    public void createPerson() throws Exception {
        // Validate the database is empty
        assertThat(personRepository.findAll()).hasSize(0);

        // Create the Person
        restPersonMockMvc.perform(post("/app/rest/persons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> persons = personRepository.findAll();
        assertThat(persons).hasSize(1);
        Person testPerson = persons.iterator().next();
        assertThat(testPerson.getName()).isEqualTo(person.getName());
        assertThat(testPerson.getPhoto()).isEqualTo(person.getPhoto());
        assertThat(testPerson.getResume()).isEqualTo(person.getResume());
        assertThat(testPerson.isSpeaker()).isEqualTo(person.isSpeaker());
        assertThat(testPerson.isTeamMember()).isEqualTo(person.isTeamMember());

        assertThat(testPerson.getAddresses()).hasSize(3);
        List<Address> testAddresses = new ArrayList<>(testPerson.getAddresses());
        Collections.sort(testAddresses);

        Address testGithub = testAddresses.get(0);
        assertThat(testGithub.getType()).isEqualTo(github.getType());
        assertThat(testGithub.getValue()).isEqualTo(github.getValue());

        Address testMail = testAddresses.get(1);
        assertThat(testMail.getType()).isEqualTo(mail.getType());
        assertThat(testMail.getValue()).isEqualTo(mail.getValue());

        Address testTwitter = testAddresses.get(2);
        assertThat(testTwitter.getType()).isEqualTo(twitter.getType());
        assertThat(testTwitter.getValue()).isEqualTo(twitter.getValue());
    }

    @Test
    @Transactional
    public void getAllPersons() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the persons
        restPersonMockMvc.perform(get("/app/rest/persons"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[0].id").value(person.getId().intValue()))
            .andExpect(jsonPath("$.[0].name").value(person.getName()))
            .andExpect(jsonPath("$.[0].photo").value(person.getPhoto()))
            .andExpect(jsonPath("$.[0].resume").value(person.getResume()))
            .andExpect(jsonPath("$.[0].speaker").value(person.isSpeaker()))
            .andExpect(jsonPath("$.[0].teamMember").value(person.isTeamMember()))
            .andExpect(jsonPath("$.[0].addresses[?(@.type == 'Github' && @.value == 'john_doe')]").exists())
            .andExpect(jsonPath("$.[0].addresses[?(@.type == 'Twitter' && @.value == '@johndoe')]").exists())
            .andExpect(jsonPath("$.[0].addresses[?(@.type == 'Mail' && @.value == 'john.doe@gmail.com')]").exists());
    }

    @Test
    @Transactional
    public void getPerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get the person
        restPersonMockMvc.perform(get("/app/rest/persons/{id}", person.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(person.getId().intValue()))
            .andExpect(jsonPath("$.name").value(person.getName()))
            .andExpect(jsonPath("$.photo").value(person.getPhoto()))
            .andExpect(jsonPath("$.resume").value(person.getResume()))
            .andExpect(jsonPath("$.speaker").value(person.isSpeaker()))
            .andExpect(jsonPath("$.teamMember").value(person.isTeamMember()))
            .andExpect(jsonPath("$.addresses[?(@.type == 'Github' && @.value == 'john_doe')]").exists())
            .andExpect(jsonPath("$.addresses[?(@.type == 'Twitter' && @.value == '@johndoe')]").exists())
            .andExpect(jsonPath("$.addresses[?(@.type == 'Mail' && @.value == 'john.doe@gmail.com')]").exists());
    }

    @Test
    @Transactional
    public void getNonExistingPerson() throws Exception {
        // Get the person
        restPersonMockMvc.perform(get("/app/rest/persons/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Update the person
        person.setName("Jane Doe");
        person.setPhoto("updated photo");
        person.setResume(null);
        person.setSpeaker(false);
        person.setTeamMember(true);
        person.getAddresses().clear();
        mail.setValue("jane.doe@gmail.com");
        person.getAddresses().add(mail);

        restPersonMockMvc.perform(post("/app/rest/persons")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> persons = personRepository.findAll();
        assertThat(persons).hasSize(1);
        Person testPerson = persons.iterator().next();
        assertThat(testPerson.getName()).isEqualTo(person.getName());
        assertThat(testPerson.getPhoto()).isEqualTo(person.getPhoto());
        assertThat(testPerson.getResume()).isEqualTo(person.getResume());
        assertThat(testPerson.isSpeaker()).isEqualTo(person.isSpeaker());
        assertThat(testPerson.isTeamMember()).isEqualTo(person.isTeamMember());

        assertThat(testPerson.getAddresses()).hasSize(1);

        Address testMail = testPerson.getAddresses().iterator().next();
        assertThat(testMail.getType()).isEqualTo(mail.getType());
        assertThat(testMail.getValue()).isEqualTo(mail.getValue());
    }

    @Test
    @Transactional
    public void deletePerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get the person
        restPersonMockMvc.perform(delete("/app/rest/persons/{id}", person.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Person> persons = personRepository.findAll();
        assertThat(persons).hasSize(0);

        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses).hasSize(0);
    }
}
