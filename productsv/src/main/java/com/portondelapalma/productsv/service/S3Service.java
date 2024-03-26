package com.portondelapalma.productsv.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service implements IFileService {

    @Value("${bucketName}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3;

    @Override
    public String saveFile(MultipartFile file) {
        UUID uuid = UUID.randomUUID();
        String originalFilename = uuid.toString() + "_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3.putObject(new PutObjectRequest(bucketName, originalFilename, file.getInputStream(), metadata));
            return s3.getUrl(bucketName, originalFilename).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteFile(String filename) {
        s3.deleteObject(bucketName, filename);
        return "Archivo eliminado";
    }

    @Override
    public List<String> listAllFiles() {
        ListObjectsV2Result listObjectsV2Result = s3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }
}
