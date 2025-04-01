package Continuing.our.introduction.to.SQL.controller;

import Continuing.our.introduction.to.SQL.model.Faculty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;


    private String baseUrl() {
        return "http://localhost:" + port + "/faculty";
    }


    @Test
    public void testCreateFaculty() {
        Faculty newFaculty = new Faculty("Slytherin", "green");

        ResponseEntity<Faculty> response = testRestTemplate.postForEntity(
                baseUrl(),
                newFaculty,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Slytherin");
        assertThat(created.getColor()).isEqualTo("green");
    }

    @Test
    public void testUpdateFaculty() {
        Faculty original = testRestTemplate.postForEntity(
                baseUrl(),
                new Faculty("Ravenclaw", "blue"),
                Faculty.class
        ).getBody();

        Faculty updatedData = new Faculty("Ravenclaw", "navy blue");

        testRestTemplate.put(
                baseUrl() + "/" + original.getId(),
                updatedData
        );

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(
                baseUrl() + "/" + original.getId(),
                Faculty.class
        );

        Faculty updated = response.getBody();
        assertThat(updated.getColor()).isEqualTo("navy blue");
    }

    @Test
    public void testDeleteFaculty() {
        Faculty faculty = testRestTemplate.postForEntity(
                baseUrl(),
                new Faculty("Hufflepuff", "yellow"),
                Faculty.class
        ).getBody();

        testRestTemplate.delete(baseUrl() + "/" + faculty.getId());

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(
                baseUrl() + "/" + faculty.getId(),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testFacultyNotFound() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                baseUrl() + "/999999",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}