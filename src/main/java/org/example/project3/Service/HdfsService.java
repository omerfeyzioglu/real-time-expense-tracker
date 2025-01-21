package org.example.project3.Service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

@Service
@Slf4j
public class HdfsService {
    private static final String HDFS_URI = "hdfs://localhost:9000";  // HDFS URI
    private static final String IMAGE_DIR = "/images/";  // Images folder path

    private FileSystem fileSystem;

    public HdfsService() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000");  // HDFS adresi
        fileSystem = FileSystem.get(conf);
    }

    public void downloadImage(String imageName, OutputStream outputStream) throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(HDFS_URI), conf);
        Path filePath = new Path(IMAGE_DIR + imageName);

        if (fs.exists(filePath)) {
            InputStream in = fs.open(filePath);
            IOUtils.copyBytes(in, outputStream, 4096, false);
        } else {
            throw new Exception("File not found on HDFS.");
        }
    }

    public void uploadImage(String fileName, InputStream inputStream) throws IOException {
        Path path = new Path(IMAGE_DIR + fileName);

        // Dizin kontrolü
        Path directory = new Path(IMAGE_DIR);
        if (!fileSystem.exists(directory)) {
            fileSystem.mkdirs(directory);
        }

        // Dosyayı HDFS'e yükle
        FSDataOutputStream outputStream = fileSystem.create(path);

        // Değişiklik burada
        IOUtils.copyBytes(inputStream, outputStream, 4096, true);
    }

    public void deleteImage(String imageName) throws IOException {
        if (imageName == null || imageName.trim().isEmpty()) {
            log.warn("Attempted to delete image with null or empty name");
            return;
        }

        try {
            Path filePath = new Path(IMAGE_DIR + imageName);

            // Check if file exists before attempting deletion
            if (fileSystem.exists(filePath)) {
                boolean deleted = fileSystem.delete(filePath, false);  // false means don't recursively delete

                if (deleted) {
                    log.info("Successfully deleted image: {}", imageName);
                } else {
                    log.error("Failed to delete image: {}", imageName);
                    throw new IOException("Failed to delete image: " + imageName);
                }
            } else {
                log.warn("Image not found for deletion: {}", imageName);
            }
        } catch (IOException e) {
            log.error("Error deleting image {}: {}", imageName, e.getMessage());
            throw new IOException("Error deleting image: " + imageName, e);
        }
    }
}