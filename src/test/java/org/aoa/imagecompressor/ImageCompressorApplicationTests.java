package org.aoa.imagecompressor;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import org.aoa.imagecompressor.service.FileService;
import org.aoa.imagecompressor.service.compressor.JpegCompressor;
import org.aoa.imagecompressor.service.compressor.PngCompressor;
import org.aoa.imagecompressor.service.compressor.WebpCompressor;
import org.aoa.imagecompressor.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ImageCompressorApplicationTests {

    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    private FileService fileService = new FileServiceImpl(gridFsTemplate);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInvalidImage(){
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "image/plain", "test".getBytes());
        Exception exception = assertThrows(IOException.class, () -> fileService.addFile("testfile", file));
        assertEquals("Failed to read image from file.", exception.getMessage());
    }

    @Test
    void testInvalidContentType(){
        MockMultipartFile invalidTypeFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> fileService.addFile("testfile", invalidTypeFile));
        assertEquals("File is not a valid image file.", exception.getMessage());
    }

    @Test
    void testCompressPngtasticPng_ValidInput() throws Exception {
        InputStream originalImage = new ByteArrayInputStream("valid png data".getBytes());
        ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
        compressedOutput.write("compressed data".getBytes());

        PngImage mockInputImage = mock(PngImage.class);
        PngImage mockOptimizedImage = mock(PngImage.class);
        PngOptimizer mockOptimizer = mock(PngOptimizer.class);

        when(mockOptimizer.optimize(mockInputImage, false, 5)).thenReturn(mockOptimizedImage);
        doAnswer(invocation -> {
            compressedOutput.writeTo((ByteArrayOutputStream) invocation.getArgument(0));
            return null;
        }).when(mockOptimizedImage).writeDataOutputStream(any(ByteArrayOutputStream.class));

        PngImage inputImage = new PngImage(originalImage);
        PngOptimizer optimizer = new PngOptimizer();
        InputStream compressedStream = PngCompressor.compressPngtasticPng(originalImage, 5);

        assertNotNull(compressedStream);
        assertTrue(compressedStream.available() > 0);
    }

    @Test
    void testCompressImageIOPng() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/оригинал.png");
        BufferedImage image = ImageIO.read(resource.getFile());
        float compressionQuality = 0.7f;

        InputStream resultStream = PngCompressor.compressImageIOPng(image, compressionQuality);
        assertNotNull(resultStream, "The result stream is null");
        assert(resultStream.available() > 0);

        BufferedImage compressedImage = ImageIO.read(resultStream);
        assertNotNull(compressedImage, "The compressed image is null");
        assert(compressedImage.getWidth() == image.getWidth());
        assert(compressedImage.getHeight() == image.getHeight());
    }


    @Test
    void testCompressImageIOJpeg_ValidInput() throws Exception {
        ClassPathResource resource = new ClassPathResource("static/оригинал.jpeg");
        BufferedImage testImage = ImageIO.read(resource.getInputStream());

        InputStream compressedImage = JpegCompressor.compressImageIOJpeg(testImage, 0.5f);

        assertNotNull(compressedImage);
        assertTrue(compressedImage.available() > 0, "Compressed image should have content.");
    }

    @Test
    void testCompressImageIOJpeg_NullInput() {
        assertThrows(NullPointerException.class, () -> {
            JpegCompressor.compressImageIOJpeg(null, 0.5f);
        });
    }

    @Test
    void testCompressImageIOJpeg_InvalidCompressionQuality() throws Exception {
        ClassPathResource resource = new ClassPathResource("static/оригинал.jpeg");
        BufferedImage testImage = ImageIO.read(resource.getInputStream());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            JpegCompressor.compressImageIOJpeg(testImage, 1.5f);
        });

        assertEquals("Quality out of bounds!", exception.getMessage());
    }

    @Test
    void testCompressImageIOWebp_ValidInput() throws Exception {
        // Create a simple BufferedImage for testing
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        InputStream compressedImage= WebpCompressor.compressImageIOWebp(testImage, 0.5f);

        assertNotNull(compressedImage, "Compressed image should not be null.");
        assertTrue(compressedImage.available() > 0, "Compressed image should have content.");

        // Optionally, attempt to decode the WebP output using an external library to validate format
    }

    @Test
    void testCompressImageIOWebp_NullImage() {
        assertThrows(NullPointerException.class, () -> {
            WebpCompressor.compressImageIOWebp(null, 0.5f);
        });
    }

    @Test
    void testCompressImageIOWebp_InvalidCompressionQuality() throws Exception {
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WebpCompressor.compressImageIOWebp(testImage, 1.5f);
        });
        assertEquals("Compression quality must be between 0 and 1", exception.getMessage());

        Exception exceptionNegative = assertThrows(IllegalArgumentException.class, () -> {
            WebpCompressor.compressImageIOWebp(testImage, -0.1f);
        });
        assertEquals("Compression quality must be between 0 and 1", exceptionNegative.getMessage());
    }

    @Test
    void testCompressImageIOWebp_CompressionQualityEffect() throws Exception {
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        // Compress with high quality
        InputStream highQualityStream = WebpCompressor.compressImageIOWebp(testImage, 1.0f);
        byte[] highQualityBytes = highQualityStream.readAllBytes();

        // Compress with low quality
        InputStream lowQualityStream = WebpCompressor.compressImageIOWebp(testImage, 0.1f);
        byte[] lowQualityBytes = lowQualityStream.readAllBytes();

        assertTrue(lowQualityBytes.length < highQualityBytes.length,
                "Low quality compression should result in a smaller file size.");
    }
}
