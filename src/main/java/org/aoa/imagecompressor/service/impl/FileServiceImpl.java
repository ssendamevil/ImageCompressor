package org.aoa.imagecompressor.service.impl;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.aoa.imagecompressor.service.FileService;
import org.aoa.imagecompressor.service.compressor.ImageCompressor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
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
    private final GridFsOperations gridFsOperations;

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

    private InputStream compressFile(MultipartFile file) throws IOException {

        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        if (inputImage == null) {
            throw new IOException("Failed to read image from file.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File is not a valid image.");
        }

        if (contentType.equals("image/png")) {
            return ImageCompressor.compressPng(inputImage, 0.9f, "PNG");
        }else if(contentType.equals("image/jpeg")) {
            return ImageCompressor.compressJpeg(file.getInputStream());
        }else {
            return ImageCompressor.compressWebp(inputImage);
        }
    }
}
