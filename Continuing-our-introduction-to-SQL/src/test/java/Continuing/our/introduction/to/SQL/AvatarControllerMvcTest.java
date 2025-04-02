package Continuing.our.introduction.to.SQL;

import Continuing.our.introduction.to.SQL.controller.AvatarController;
import Continuing.our.introduction.to.SQL.exception.AvatarNotFoundException;
import Continuing.our.introduction.to.SQL.model.Avatar;
import Continuing.our.introduction.to.SQL.service.AvatarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvatarController.class)
class AvatarControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvatarService avatarService;

    private final String testImagePath = "src/test/resources/test-avatar.jpg";

    @Test
    void uploadAvatar_ShouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(Path.of(testImagePath))
        );

        mockMvc.perform(multipart("/avatars/1/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Avatar uploaded successfully"));
    }

    @Test
    void getPreview_ShouldReturnImage() throws Exception {
        Avatar mockAvatar = new Avatar();
        mockAvatar.setData(Files.readAllBytes(Path.of(testImagePath)));
        mockAvatar.setMediaType(MediaType.IMAGE_JPEG_VALUE);

        when(avatarService.findAvatar(1L)).thenReturn(mockAvatar);

        mockMvc.perform(MockMvcRequestBuilders.get("/avatars/1/preview"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.content().bytes(mockAvatar.getData()));
    }

    @Test
    void getPreview_ShouldHandleNotFound() throws Exception {
        when(avatarService.findAvatar(999L))
                .thenThrow(new AvatarNotFoundException("Avatar not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/avatars/999/preview"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Avatar not found"));
    }

    @Test
    void getAllAvatars_ShouldReturnPaginated() throws Exception {
        Page<Avatar> page = new PageImpl<>(List.of(new Avatar(), new Avatar()));
        when(avatarService.getAllAvatars(0, 2)).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/avatars?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2));
    }

    @Test
    void uploadAvatarTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(Path.of(testImagePath))
        );

        mockMvc.perform(multipart("/avatars/1/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("Avatar uploaded successfully"));
    }
}
