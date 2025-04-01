package Continuing.our.introduction.to.SQL.controller;

import Continuing.our.introduction.to.SQL.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerRestTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;


    private String baseUrl() {
        return "http://localhost:" + port + "/students";
    }


    @Test
    public void testCreateStudent() {
        Student newStudent = new Student(16, "Godrick Griffindor");

        ResponseEntity<Student> response = testRestTemplate.postForEntity(
                baseUrl(),
                newStudent,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Godrick Griffindor");
        assertThat(created.getAge()).isEqualTo(16);
    }

    @Test
    public void testUpdateStudent() {
        Student original = testRestTemplate.postForEntity(
                baseUrl(),
                new Student(55, "Minerva"),
                Student.class
        ).getBody();

        Student updatedData = new Student(56, "Minerva");

        testRestTemplate.put(
                baseUrl() + "/" + original.getId(),
                updatedData
        );

        ResponseEntity<Student> response = testRestTemplate.getForEntity(
                baseUrl() + "/" + original.getId(),
                Student.class
        );

        Student updated = response.getBody();
        assertThat(updated.getName()).isEqualTo("Minerva");
        assertThat(updated.getAge()).isEqualTo(56);
    }

    @Test
    public void testDeleteStudent() {
        Student student = testRestTemplate.postForEntity(
                baseUrl(),
                new Student(15, "Ron"),
                Student.class
        ).getBody();

        testRestTemplate.delete(baseUrl() + "/" + student.getId());

        ResponseEntity<Student> response = testRestTemplate.getForEntity(
                baseUrl() + "/" + student.getId(),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testStudentNotFound() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                baseUrl() + "/999999",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
