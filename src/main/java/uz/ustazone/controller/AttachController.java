package uz.ustazone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ustazone.service.AttachService;

@RestController
@RequestMapping("/api/v1/attach")
@Tag(name = "Attach API", description = "API list for attach")
public class AttachController {

    @Autowired
    private AttachService attachService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload", description = "API used for uploading files")
    public ResponseEntity<String> create(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attachService.upload(file));
    }
    @GetMapping("/open/{fileId}")
    @Operation(summary = "Open", description = "API used for open file")
    public ResponseEntity<Resource> open(@PathVariable String fileId) {
        return attachService.open(fileId);
    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download", description = "API used for download files")
    public ResponseEntity<Resource> download(@PathVariable String fileId) {
        return attachService.download(fileId);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete", description = "API used for deleting files")
    public ResponseEntity<Boolean> delete(@PathVariable String fileId) {
        return ResponseEntity.ok(attachService.delete(fileId));
    }
}
