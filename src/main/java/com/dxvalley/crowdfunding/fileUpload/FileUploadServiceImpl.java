package com.dxvalley.crowdfunding.fileUpload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dxvalley.crowdfunding.exception.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    /**
     * Uploads a file.
     *
     * @param multipartFile The file to upload
     * @return The public URL of the uploaded file
     * @throws BadRequestException If an error occurs during the upload process, such as an invalid file size or format
     * @throws InternalServerErrorException If an internal server error occurs during the upload process
     */
    public String uploadFile(MultipartFile multipartFile) {
        try {
            File uploadedFile = convertMultiPartToFile(multipartFile);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());

            return uploadResult.get("url").toString();
        } catch (IOException ex) {
            log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                    getClass().getSimpleName(),
                    "uploadFile",
                    ex.getMessage());

            throw new BadRequestException("Invalid file size or format detected!");
        } catch (Exception ex) {
            log.error("An error occurred in {}.{} while uploading the file. Details: {}",
                    getClass().getSimpleName(),
                    "uploadFile",
                    ex.getMessage());

            throw new InternalServerErrorException("An internal server error occurred during file upload!");
        }
    }


    /**
     * Uploads a list of files.
     *
     * @param files The list of files to upload
     * @return A list of public IDs of the uploaded files
     * @throws BadRequestException If the provided files list is null or empty
     */
    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            List<String> uploadedFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                String publicId = uploadFile(file);
                uploadedFiles.add(publicId);
            }
            return uploadedFiles;
        }

        throw new BadRequestException("The files list must not be null or empty");
    }


    /**
     * Converts a MultipartFile to a File object.
     *
     * @param file The MultipartFile to be converted.
     * @return The converted File object.
     * @throws IOException If there is an error during the conversion.
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
