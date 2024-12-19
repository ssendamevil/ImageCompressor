package org.aoa.imagecompressor.service.compressor;

import com.luciad.imageio.webp.WebPWriteParam;

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

public class WebpCompressor {
    public static InputStream compressImageIOWebp(BufferedImage image, Float compressionQuality) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed)){
            ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();
            WebPWriteParam webPWriteParam = new WebPWriteParam(writer.getLocale());
            webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            webPWriteParam.setCompressionType(webPWriteParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            webPWriteParam.setCompressionQuality(compressionQuality);
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(image, null, null), webPWriteParam);
            writer.dispose();
        }
        return new ByteArrayInputStream(compressed.toByteArray());
    }
}
