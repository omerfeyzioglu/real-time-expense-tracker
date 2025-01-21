package org.example.project3.Service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.net.URI;

public class HDFSImageService {

    private FileSystem fileSystem;

    public HDFSImageService(String hdfsUri) throws IOException {
        fileSystem = FileSystem.get(URI.create(hdfsUri), new Configuration());
    }

    public void processImages() throws IOException {
        Path path = new Path("/images"); // resimlerin bulunduğu HDFS klasörü
        FileStatus[] fileStatuses = fileSystem.listStatus(path);

        for (FileStatus fileStatus : fileStatuses) {
            String fileName = fileStatus.getPath().getName();
            System.out.println("Processing image: " + fileName);
            // Burada resimleri işleyebilir ve veritabanına ekleyebilirsiniz
        }
    }

    public void close() throws IOException {
        fileSystem.close();
    }
}
