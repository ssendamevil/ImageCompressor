package org.aoa.imagecompressor.service.compressor;

import com.luciad.imageio.webp.WebPWriteParam;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
public class ImageCompressor {

    public static InputStream compressPng(BufferedImage originalImage,Float compressValue, String fileType) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();

        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed)) {

            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("JPEG").next();

            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();

            if (fileType.equals("jpg")) {

                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(compressValue);
                jpgWriter.setOutput(outputStream);
                jpgWriter.write(null, new IIOImage(originalImage, null, null), jpgWriteParam);
                jpgWriter.dispose();

            } else {

                BufferedImage newBufferedImage = new BufferedImage(
                        originalImage.getWidth(),
                        originalImage.getHeight(),
                        BufferedImage.TYPE_INT_BGR);

                newBufferedImage.createGraphics()
                        .drawImage(newBufferedImage, 0, 0, Color.white, null);

                Graphics2D g2d = newBufferedImage.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, newBufferedImage.getWidth(), newBufferedImage.getHeight());
                g2d.drawImage(originalImage, 0, 0, null);
                g2d.dispose();

                newBufferedImage.flush();

                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(compressValue);

                jpgWriter.setOutput(outputStream);
                jpgWriter.write(null, new IIOImage(newBufferedImage, null, null), jpgWriteParam);
                jpgWriter.dispose();

            }
        }
        return new ByteArrayInputStream(compressed.toByteArray());
    }

    public static InputStream compressJpeg(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(inputStream)
                .scale(1)
                .outputQuality(0.2)
                .toOutputStream(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static InputStream compressWebp(BufferedImage image) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed)){
            ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();
            WebPWriteParam webPWriteParam = new WebPWriteParam(writer.getLocale());
            webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            webPWriteParam.setCompressionType(webPWriteParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            webPWriteParam.setCompressionQuality(0.7f);
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(image, null, null), webPWriteParam);
            writer.dispose();
        }
        return new ByteArrayInputStream(compressed.toByteArray());
    }
}
