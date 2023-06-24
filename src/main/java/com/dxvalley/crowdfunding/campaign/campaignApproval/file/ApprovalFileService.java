package com.dxvalley.crowdfunding.campaign.campaignApproval.file;

import com.dxvalley.crowdfunding.fileUploadManager.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalFileService {
    private final ApprovalFileRepository approvalFileRepository;
    private final FileUploadService fileUploadService;
    private final DateTimeFormatter dateTimeFormatter;

    public List<ApprovalFile> getAllApprovalFiles() {
        return this.approvalFileRepository.findAll();
    }

    public List<ApprovalFile> saveApprovalFile(List<MultipartFile> approvalFiles) {
        List<ApprovalFile> approvalFileList = new ArrayList();
        if (approvalFiles != null && !approvalFiles.isEmpty()) {
            Iterator iterator = approvalFiles.iterator();

            while (iterator.hasNext()) {
                MultipartFile file = (MultipartFile) iterator.next();
                ApprovalFile approvalFile = this.createApprovalFile(file);
                approvalFileList.add(approvalFile);
            }
        }

        return this.approvalFileRepository.saveAll(approvalFileList);
    }

    private ApprovalFile createApprovalFile(MultipartFile file) {
        String fileUrl = this.fileUploadService.uploadFile(file);
        ApprovalFile approvalFile = new ApprovalFile();
        approvalFile.setFileUrl(fileUrl);
        approvalFile.setFileName(file.getOriginalFilename());
        approvalFile.setFileType(file.getContentType());
        approvalFile.setCreatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        return approvalFile;
    }

    public void deleteApprovalFileById(Long fileId) {
        this.approvalFileRepository.deleteById(fileId);
    }

}

