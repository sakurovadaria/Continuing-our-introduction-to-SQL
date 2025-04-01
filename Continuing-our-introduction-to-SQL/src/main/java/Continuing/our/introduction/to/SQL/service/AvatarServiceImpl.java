package Continuing.our.introduction.to.SQL.service;

import Continuing.our.introduction.to.SQL.exception.AvatarNotFoundException;
import Continuing.our.introduction.to.SQL.exception.StudentNotFoundException;
import Continuing.our.introduction.to.SQL.model.Avatar;
import Continuing.our.introduction.to.SQL.model.Student;
import Continuing.our.introduction.to.SQL.repository.AvatarRepository;
import Continuing.our.introduction.to.SQL.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class AvatarServiceImpl implements AvatarService {


    @Autowired
    private final AvatarRepository avatarRepository;

    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarServiceImpl(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    @Transactional
    @Override
    public void uploadAvatar(Long id, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));

        String extension = getExtension(file.getOriginalFilename());
        String fileName = id + "_avatar." + extension;
        Path filePath = Path.of(avatarsDir, fileName);

        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        file.transferTo(filePath);

        Avatar avatar = avatarRepository.findByStudentId(id)
                .orElseGet(Avatar::new);

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());

        if (file.getContentType().startsWith("image/")) {
            avatar.setData(generatePreview(filePath));
        } else {
            avatar.setData(Files.readAllBytes(filePath));
        }

        avatarRepository.save(avatar);
    }


    private byte[] generatePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath)) {
            BufferedImage image = ImageIO.read(is);

            // Проверяем, что изображение успешно загружено
            if (image == null) {
                throw new IOException("Failed to read image from file: " + filePath);
            }

            // Генерация превью (уменьшение размера)
            int previewWidth = 100;
            int previewHeight = (int) ((double) image.getHeight() / image.getWidth() * previewWidth);
            BufferedImage preview = new BufferedImage(previewWidth, previewHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = preview.createGraphics();
            g2d.drawImage(image, 0, 0, previewWidth, previewHeight, null);
            g2d.dispose();

            // Сохранение превью в байтовый массив
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }


    private String getExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf(".");
        if (extensionIndex > 0) {
            return fileName.substring(extensionIndex + 1);
        }
        return "";
    }

    @Override
    public Avatar findAvatar(Long studentId) throws AvatarNotFoundException {
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new AvatarNotFoundException("Avatar not found for student ID: " + studentId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @Override
    public Page<Avatar> getAllAvatars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return avatarRepository.findAll(pageable);
    }
}

