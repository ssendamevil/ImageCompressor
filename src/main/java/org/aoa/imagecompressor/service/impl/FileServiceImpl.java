package org.aoa.imagecompressor.service.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.AllArgsConstructor;
import org.aoa.imagecompressor.service.FileService;
import org.aoa.imagecompressor.service.compressor.JpegCompressor;
import org.aoa.imagecompressor.service.compressor.PngCompressor;
import org.aoa.imagecompressor.service.compressor.WebpCompressor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@AllArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    private final GridFsTemplate gridFsTemplate;

    @Override
    public String addFile(String file, MultipartFile filepart) throws IOException, InterruptedException {
        DBObject metaData = new BasicDBObject();
        metaData.put("file", file);
        metaData.put("type", "image");
        InputStream stream = compressFile(filepart);
        Object id = gridFsTemplate.store(stream, file, filepart.getContentType(), metaData);
        return id.toString();
    }

    @Override
    public GridFsResource readFile(String fileId) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        assert gridFSFile != null;
        return gridFsTemplate.getResource(gridFSFile);
    }

    private InputStream compressFile(MultipartFile file) throws IOException, IllegalArgumentException {
        InputStream inputStream = file.getInputStream();
        BufferedImage inputImage = ImageIO.read(inputStream);
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File is not a valid image file.");
        }

        if (inputImage == null) {
            throw new IOException("Failed to read image from file.");
        }

        if (contentType.equals("image/png")) {
            return PngCompressor.compressImageUsingPngQuant(file.getInputStream());
        }else if(contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
            return JpegCompressor.compressThumbnailsJpeg(file.getInputStream(), 0.9);
        }else {
            return WebpCompressor.compressImageIOWebp(inputImage, 0.9f);
        }
    }
}
