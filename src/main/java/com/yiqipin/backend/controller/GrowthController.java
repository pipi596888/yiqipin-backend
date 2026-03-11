package com.yiqipin.backend.controller;

import com.yiqipin.backend.common.ApiResponse;
import com.yiqipin.backend.entity.Campaign;
import com.yiqipin.backend.service.CampaignService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GrowthController {

    private final CampaignService campaignService;

    public GrowthController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/marketing/campaigns")
    public ApiResponse<Map<String, List<Campaign>>> campaigns() {
        List<Campaign> list = campaignService.listActive();
        Map<String, List<Campaign>> data = new HashMap<>();
        data.put("list", list);
        return ApiResponse.success(data);
    }

    @GetMapping("/recommendation/home")
    public ApiResponse<Map<String, Object>> recommendations() {
        // Placeholder for recommendation system
        Map<String, Object> data = new HashMap<>();
        data.put("hot_list", List.of());
        data.put("guess_list", List.of());
        return ApiResponse.success(data);
    }

    @GetMapping("/analytics/overview")
    public ApiResponse<Map<String, Object>> analytics() {
        // Placeholder for analytics
        Map<String, Object> data = new HashMap<>();
        data.put("salesToday", 0.0);
        data.put("salesYesterday", 0.0);
        data.put("orderCountToday", 0);
        data.put("orderCountYesterday", 0);
        data.put("userGrowthToday", 0);
        data.put("userGrowthYesterday", 0);
        data.put("topProducts", List.of());
        return ApiResponse.success(data);
    }
}
