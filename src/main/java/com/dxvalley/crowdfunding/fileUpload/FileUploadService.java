package com.dxvalley.crowdfunding.fileUpload;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
     String uploadFile(MultipartFile multipartFile);
}
