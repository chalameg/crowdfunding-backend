package com.dxvalley.crowdfunding.service;

import org.springframework.web.multipart.MultipartFile;


public interface FileUploadService {
     String uploadFile(MultipartFile img);
     String uploadFileVideo(MultipartFile video);
}
