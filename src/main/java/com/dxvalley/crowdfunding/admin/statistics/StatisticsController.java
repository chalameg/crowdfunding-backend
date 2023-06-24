package com.dxvalley.crowdfunding.admin.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/admin/statistics"})
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;
    @GetMapping
    ResponseEntity<StatisticsData> getStatistics() {
        return ResponseEntity.ok(this.statisticsService.getStatistics());
    }

}
