package com.dxvalley.crowdfunding.admin.general;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DashBoardController {
    private final DashBoardService dashBoardService;
    @GetMapping
    ResponseEntity<?> getStatistics() {
        return new ResponseEntity<>(dashBoardService.getStatistics(), HttpStatus.OK);
    }
}

