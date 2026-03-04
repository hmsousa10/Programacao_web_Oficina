package com.oficina.sgo.controller;

import com.oficina.sgo.dto.response.DashboardResponse;
import com.oficina.sgo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    public ResponseEntity<DashboardResponse> getKpis() {
        return ResponseEntity.ok(dashboardService.getKpis());
    }
}
