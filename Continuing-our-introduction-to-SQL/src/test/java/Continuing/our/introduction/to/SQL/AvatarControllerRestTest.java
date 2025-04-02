package Continuing.our.introduction.to.SQL;

import Continuing.our.introduction.to.SQL.model.Avatar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AvatarControllerRestTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private final String testImagePath = "src/test/resources/test-avatar.jpg";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void uploadAndGetAvatar_ShouldWork() throws IOException {
        File file = new File(testImagePath);

        // Тест загрузки аватара
        testRestTemplate.postForObject(baseUrl + "/avatars/1/avatar", file, String.class);

        // Тест получения превью
        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(baseUrl + "/avatars/1/preview", byte[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);

        // Тест получения полной версии
        response = testRestTemplate.getForEntity(baseUrl + "/avatars/1/full", byte[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
    }

    @Test
    void getNonExistingAvatar_ShouldReturn404() {
        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(baseUrl + "/avatars/999/preview", byte[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllAvatars_ShouldReturnPaginated() {
        ResponseEntity<Page<Avatar>> response = testRestTemplate.exchange(
                baseUrl + "/avatars?page=0&size=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Page<Avatar>>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent().size()).isEqualTo(2);
    }
}