package com.dxvalley.crowdfunding.fileUploadManager;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    private final Cloudinary cloudinaryConfig;

    public String uploadFile(MultipartFile multipartFile) {
        try {
            File uploadedFile = this.convertMultiPartToFile(multipartFile);
            Map uploadResult = this.cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException ex) {
            log.error("An error occurred in {}.{} while accessing the database. Details: {}", new Object[]{this.getClass().getSimpleName(), "uploadFile", ex.getMessage()});
            throw new BadRequestException("Invalid file size or format detected!");
        } catch (Exception ex) {
            log.error("An error occurred in {}.{} while uploading the file. Details: {}", new Object[]{this.getClass().getSimpleName(), "uploadFile", ex.getMessage()});
            throw new RuntimeException("An internal server error occurred during file upload!");
        }
    }

    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            List<String> uploadedFiles = new ArrayList();
            Iterator iterator = files.iterator();

            while (iterator.hasNext()) {
                MultipartFile file = (MultipartFile) iterator.next();
                String publicId = this.uploadFile(file);
                uploadedFiles.add(publicId);
            }

            return uploadedFiles;
        } else {
            throw new BadRequestException("The files list must not be null or empty");
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);

        try {
            fos.write(file.getBytes());
        } catch (Throwable throwable) {
            try {
                fos.close();
            } catch (Throwable var6) {
                throwable.addSuppressed(var6);
            }

            throw throwable;
        }

        fos.close();
        return convFile;
    }
}
