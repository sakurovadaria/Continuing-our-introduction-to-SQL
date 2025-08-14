package Continuing.our.introduction.to.SQL.controller;

import Continuing.our.introduction.to.SQL.exception.AvatarNotFoundException;
import Continuing.our.introduction.to.SQL.model.Avatar;
import Continuing.our.introduction.to.SQL.service.AvatarService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RequestMapping("/avatars")
@RestController
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }


    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {

        avatarService.uploadAvatar(id, file);
        return ResponseEntity.ok("Avatar uploaded successfully");
    }

    @GetMapping(value = "/{studentId}/preview")
    public ResponseEntity<byte[]> getPreview(@PathVariable Long studentId) {
        try {
            Avatar avatar = avatarService.findAvatar(studentId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                    .body(avatar.getData());
        } catch (AvatarNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }

    @ExceptionHandler({AvatarNotFoundException.class, IOException.class})
    public ResponseEntity<String> handleException(Exception ex) {
        if (ex instanceof AvatarNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping
    public Page<Avatar> getAllAvatars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return avatarService.getAllAvatars(page, size);
    }


    @GetMapping(value = "/{studentId}/full")
    public void downloadFullAvatar(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(studentId);
        response.setContentType(avatar.getMediaType());
        try (InputStream is = Files.newInputStream(Path.of(avatar.getFilePath()))) {
            is.transferTo(response.getOutputStream());
        }
    }

}