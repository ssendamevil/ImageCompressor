package org.aoa.imagecompressor.service.compressor;

import net.coobird.thumbnailator.Thumbnails;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JpegCompressor {

    public static InputStream compressThumbnailsJpeg(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(inputStream)
                .scale(1)
                .outputQuality(0.1)
                .toOutputStream(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static InputStream compressImageIOJpeg(BufferedImage originalImage, Float compressQuality) throws IOException, NullPointerException {
        if(originalImage == null) {
            throw new NullPointerException("Original image is null");
        }
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed)) {
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(compressQuality);
            jpgWriter.setOutput(outputStream);
            jpgWriter.write(null, new IIOImage(originalImage, null, null), jpgWriteParam);
            jpgWriter.dispose();
        }
        return new ByteArrayInputStream(compressed.toByteArray());
    }
}
