package com.dxvalley.crowdfunding.services;

import org.springframework.web.multipart.MultipartFile;


public interface FileUploadService {
     String uploadFile(MultipartFile img);
     String uploadFileVideo(MultipartFile video);
}
