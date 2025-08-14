package Continuing.our.introduction.to.SQL;

import Continuing.our.introduction.to.SQL.service.AvatarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AvatarControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setup() throws IOException {
        // Загружаем тестовую картинку из classpath
        InputStream is = getClass().getClassLoader().getResourceAsStream("test1_avatar.jpg");
        if (is == null) {
            throw new FileNotFoundException("Файл test1_avatar.jpg не найден в classpath");
        }
        byte[] imageBytes = is.readAllBytes();
        is.close();

        // Сохраняем в переменную, чтобы использовать в тестах
        this.mockFile = new MockMultipartFile(
                "file",                     // имя параметра в контроллере
                "test1_avatar.jpg",          // имя файла
                MediaType.IMAGE_JPEG_VALUE,  // тип контента
                imageBytes                   // байты файла
        );
    }




    @Test
    public void uploadAvatar_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(avatarService).uploadAvatar(Mockito.anyLong(), Mockito.any());

        mockMvc.perform(multipart("/avatars/{id}/avatar", 1L)
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Avatar uploaded successfully"));
    }
}