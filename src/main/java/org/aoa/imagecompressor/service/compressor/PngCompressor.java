package org.aoa.imagecompressor.service.compressor;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PngCompressor implements ImageCompressor {
    public static InputStream compressImageIOPng(BufferedImage originalImage, Float compressQuality) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();

        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed)) {

            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
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
            jpgWriteParam.setCompressionType(jpgWriteParam.getCompressionType());
            jpgWriteParam.setCompressionQuality(compressQuality);

            jpgWriter.setOutput(outputStream);
            jpgWriter.write(null, new IIOImage(newBufferedImage, null, null), jpgWriteParam);
            jpgWriter.dispose();
        }
        return new ByteArrayInputStream(compressed.toByteArray());
    }

    public static InputStream compressPngtasticPng(InputStream originalImage, Integer compressValue) throws IOException {
        PngImage inputImage = new PngImage(originalImage);
        PngOptimizer optimizer = new PngOptimizer();
        PngImage optimizedImage = optimizer.optimize(inputImage, false, compressValue);

        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        optimizedImage.writeDataOutputStream(compressed);

        return new ByteArrayInputStream(compressed.toByteArray());
    }

    public static InputStream compressImageUsingPngQuant(InputStream originalImage) throws IOException {
        Path tempDir = Files.createTempDirectory("file-service");

        Path tempInputFile = Files.createTempFile(tempDir,"input", ".png");
        Path tempOutputFile = tempDir.resolve("outputFrom.png");
        Files.copy(originalImage, tempInputFile, StandardCopyOption.REPLACE_EXISTING);

        try {
            int exitCode = getExitCode(tempInputFile, tempDir);

            if (exitCode != 0) {
                throw new IOException("pngquant failed with exit code " + exitCode);
            }

            return new ByteArrayInputStream(Files.readAllBytes(tempInputFile));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Image compression was interrupted", e);
        } finally {
            Files.deleteIfExists(tempInputFile);
            Files.deleteIfExists(tempOutputFile);
        }
    }

    private static int getExitCode(Path tempInputFile, Path tempDir) throws IOException, InterruptedException {
        String[] command = {
                "docker",
                "run",
                "--rm",
                "-v",tempDir + ":/data",
                "kolyadin/pngquant",
                "--quality=90",
                "--output", "/data/outputFrom.png",
                "/data/" + tempInputFile.getFileName().toString()
        };

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(tempDir.toFile());

        Process process = builder.start();
        return process.waitFor();
    }
}
