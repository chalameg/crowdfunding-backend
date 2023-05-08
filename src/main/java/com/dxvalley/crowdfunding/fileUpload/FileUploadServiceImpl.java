package com.dxvalley.crowdfunding.fileUpload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    private final Cloudinary cloudinaryConfig;

    /**
     Uploads a file to Cloudinary and returns the URL of the uploaded file.
     @param multipartFile The MultipartFile representing the file to be uploaded.
     @return The URL of the uploaded file.
     @throws IllegalArgumentException If there is an error with the file size or format.
     */
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        try {
            File uploadedFile = convertMultiPartToFile(multipartFile);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());

            return uploadResult.get("url").toString();
        } catch (IOException ex) {
            log.error("UploadFile -> Error uploading file: {}", ex.getMessage());
            throw new IllegalArgumentException("UploadFile -> Bad file size or format!");
        }
    }

    /**
     Converts a MultipartFile to a File object.
     @param file The MultipartFile to be converted.
     @return The converted File object.
     @throws IOException If there is an error during the conversion.
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
