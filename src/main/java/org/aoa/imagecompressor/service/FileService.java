package org.aoa.imagecompressor.service;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {
    String addFile(String filename, MultipartFile file) throws IOException, InterruptedException;

    GridFsResource readFile(String fileId) throws IOException;
}
