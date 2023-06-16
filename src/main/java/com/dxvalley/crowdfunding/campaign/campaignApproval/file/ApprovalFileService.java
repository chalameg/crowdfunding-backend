package com.dxvalley.crowdfunding.campaign.campaignApproval.file;

import com.dxvalley.crowdfunding.fileUploadManager.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalFileService {
    private final ApprovalFileRepository approvalFileRepository;
    private final FileUploadService fileUploadService;
    private final DateTimeFormatter dateTimeFormatter;

    public List<ApprovalFile> getAllApprovalFiles() {
        return approvalFileRepository.findAll();
    }

    public List<ApprovalFile> saveApprovalFile(List<MultipartFile> approvalFiles) {
        List<ApprovalFile> approvalFileList = new ArrayList<>();

        if (approvalFiles != null && !approvalFiles.isEmpty()) {
            for (MultipartFile file : approvalFiles) {
                ApprovalFile approvalFile = createApprovalFile(file);
                approvalFileList.add(approvalFile);
            }
        }

        return approvalFileRepository.saveAll(approvalFileList);
    }

    private ApprovalFile createApprovalFile(MultipartFile file) {
        String fileUrl = fileUploadService.uploadFile(file);

        ApprovalFile approvalFile = new ApprovalFile();
        approvalFile.setFileUrl(fileUrl);
        approvalFile.setFileName(file.getOriginalFilename());
        approvalFile.setFileType(file.getContentType());
        approvalFile.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));

        return approvalFile;
    }


    public void deleteApprovalFileById(Long fileId) {
        approvalFileRepository.deleteById(fileId);
    }

}

