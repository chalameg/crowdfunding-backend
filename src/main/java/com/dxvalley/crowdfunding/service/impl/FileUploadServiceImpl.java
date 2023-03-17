package com.dxvalley.crowdfunding.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dxvalley.crowdfunding.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Autowired
    private Cloudinary cloudinaryConfig;

    @Override
    public String uploadFile(MultipartFile img) {
        try {
            File uploadedFile = convertMultiPartToFile(img);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            boolean isDeleted = uploadedFile.delete();

            if (isDeleted) {
                System.out.println("File successfully deleted");
            } else
                System.out.println("File doesn't exist");
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String uploadFileVideo(MultipartFile video) {
        try {
            File uploadedFile = convertMultiPartToFile(video);
            Map uploadResult = cloudinaryConfig.uploader().uploadLarge(uploadedFile, ObjectUtils.emptyMap());

            boolean isDeleted = uploadedFile.delete();

            if (isDeleted){
                System.out.println("File successfully deleted");
            }else
                System.out.println("File doesn't exist");
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
