package com.dxvalley.crowdfunding.fileUpload;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
     String uploadFile(MultipartFile multipartFile);
     List<String> uploadFiles(List<MultipartFile> files);
}
