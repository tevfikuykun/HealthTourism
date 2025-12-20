package com.healthtourism.dashboard.controller;

import com.healthtourism.dashboard.model.IntegrationStatus;
import com.healthtourism.dashboard.service.IntegrationHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class DashboardController {
    
    @Autowired
    private IntegrationHealthService healthService;
    
    @GetMapping
    public String dashboard(Model model) {
        List<IntegrationStatus> statuses = healthService.getAllStatuses();
        Map<String, Long> statistics = healthService.getStatistics();
        
        model.addAttribute("integrations", statuses);
        model.addAttribute("statistics", statistics);
        
        return "dashboard";
    }
    
    @GetMapping("/api/status")
    @ResponseBody
    public List<IntegrationStatus> getStatusJson() {
        return healthService.getAllStatuses();
    }
    
    @GetMapping("/api/statistics")
    @ResponseBody
    public Map<String, Long> getStatistics() {
        return healthService.getStatistics();
    }
}



