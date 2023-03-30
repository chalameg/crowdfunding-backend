package com.dxvalley.crowdfunding.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dxvalley.crowdfunding.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    private final Cloudinary cloudinaryConfig;
    private final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    public FileUploadServiceImpl(Cloudinary cloudinaryConfig) {
        this.cloudinaryConfig = cloudinaryConfig;
    }

    @Override
    public String uploadFile(MultipartFile img) {
        try {
            File uploadedFile = convertMultiPartToFile(img);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            if (uploadedFile.delete() == true) {
                logger.info("cloudinaryImage -> File successfully deleted");
            } else {
                logger.warn("cloudinaryImage -> File doesn't exist");
            }
            return uploadResult.get("url").toString();
        } catch (IOException ex) {
            logger.error("cloudinaryImage -> Error uploading file: {}", ex.getMessage());
            throw new IllegalArgumentException("cloudinaryImage -> Bad file size or format!");
        }
    }

    @Override
    public String uploadFileVideo(MultipartFile video) {
        try {
            File uploadedFile = convertMultiPartToFile(video);
            Map uploadResult = cloudinaryConfig.uploader().uploadLarge(uploadedFile, ObjectUtils.emptyMap());

            if (uploadedFile.delete()) {
                logger.info("cloudinaryVideo-> File successfully deleted");
            } else {
                logger.warn("cloudinaryVideo-> File doesn't exist");
            }

            return uploadResult.get("url").toString();
        } catch (IOException ex) {
            logger.error("cloudinaryVideo-> Error uploading video: {}", ex.getMessage());
            throw new IllegalArgumentException("cloudinaryVideo-> Bad video size or format!");
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
