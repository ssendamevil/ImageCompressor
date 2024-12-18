package org.aoa.imagecompressor.controller;


import lombok.RequiredArgsConstructor;
import org.aoa.imagecompressor.service.FileService;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class Controller {
    private final FileService fileService;

    @PostMapping("/upload")
    public String uploadPhoto(@RequestPart("photo") MultipartFile part) throws IOException, InterruptedException {
        return fileService.addFile("photo", part);
    }

    @GetMapping("/get/{fileId}")
    public ResponseEntity<GridFsResource> getFile(@PathVariable String fileId) throws IOException {
        GridFsResource resource = fileService.readFile(fileId);
        String contentType = resource.getOptions().getMetadata().getString("_contentType");
        String filename = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
