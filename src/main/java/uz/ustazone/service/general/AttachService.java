package uz.ustazone.service.general;

import uz.ustazone.dto.attach.AttachDTO;
import uz.ustazone.entity.AttachEntity;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class AttachService {
    @Autowired
    private AttachRepository attachRepository;
    @Value("${attache.folder}")
    private String attacheFolder;

    @Value("${server.url}")
    private String attachUrl;

    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppBadRequestException("File bosh");
        }

        try {
            String pathFolder = getYmDString(); // 2025/06/09
            String key = UUID.randomUUID().toString(); // dasdasd-dasdasda-asdasda-asdasd
            String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename())); // .jpg, .png, .mp4

            // create folder if not exists
            File folder = new File(attacheFolder + "/" + pathFolder);
            if (!folder.exists()) {
                boolean t = folder.mkdirs();
            }

            // save to system
            byte[] bytes = file.getBytes();
            Path path = Paths.get(attacheFolder + "/" + pathFolder + "/" + key + "." + extension);
            // attaches/ 2025/06/09/dasdasd-dasdasda-asdasda-asdasd.jpg
            Files.write(path, bytes);

            // save to db
            AttachEntity entity = new AttachEntity();
            entity.setId(key + "." + extension);
            entity.setPath(pathFolder);
            entity.setSize(file.getSize());
            entity.setOriginName(file.getOriginalFilename());
            entity.setType(file.getContentType());
            attachRepository.save(entity);

            return key + "." + extension;
        } catch (IOException e) {
            throw new AppBadRequestException("Upload went wrong");
        }
    }

    public ResponseEntity<Resource> open(String id) { // d5ab71b2-39a8-4ad2-80b3-729c91c932be.jpg
        AttachEntity entity = getEntity(id);
        Path filePath = Paths.get(attacheFolder + "/" + entity.getPath() + "/" + entity.getId()).normalize();
        // attaches/2025/06/09/d5ab71b2-39a8-4ad2-80b3-729c91c932be.jpg
        org.springframework.core.io.Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found: " + filePath);
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback content type
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<Resource> download(String filename) {
        AttachEntity entity = getEntity(filename);
        try {
            Path file = Paths.get(attacheFolder + "/" + entity.getPath() + "/" + entity.getId()).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                throw new AppBadRequestException("File not found");
            }
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + entity.getOriginName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            throw new AppBadRequestException("Something went wrong");
        }
    }

    public Boolean delete(String fileId) {
        AttachEntity entity = getEntity(fileId);

        try {
            Path file = Paths.get(attacheFolder + "/" + entity.getPath() + "/" + entity.getId()).normalize();
            boolean deleted = Files.deleteIfExists(file);

            if (!deleted) {
                throw new AppBadRequestException("Could not delete file from filesystem");
            }

            attachRepository.delete(entity);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new AppBadRequestException("Something went wrong");
        }
    }

    private String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    private String getExtension(String fileName) { // something.jpg
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getId());
        attachDTO.setOriginName(entity.getOriginName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setType(entity.getType());
        attachDTO.setCreatedData(entity.getCreatedDate());
        attachDTO.setPath(openURL(entity.getId()));
        return attachDTO;
    }

    public String openURL(String fileName) {
        return attachUrl + "/api/v1/attach/open/" + fileName;
    }

    public AttachEntity getEntity(String id) {
        return attachRepository.findById(id).orElseThrow(() -> new AppBadRequestException("File not found"));
    }

    public AttachEntity findById(String attachId) {
        return attachRepository.findById(attachId)
                .orElseThrow(() -> new ItemNotFoundException("Attach not found"));
    }
}
