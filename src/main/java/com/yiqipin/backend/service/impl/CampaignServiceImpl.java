package com.yiqipin.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqipin.backend.entity.Campaign;
import com.yiqipin.backend.mapper.CampaignMapper;
import com.yiqipin.backend.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl extends ServiceImpl<CampaignMapper, Campaign> implements CampaignService {

    @Override
    @Cacheable(value = "campaigns", key = "'active'")
    public List<Campaign> listActive() {
        return this.list(new LambdaQueryWrapper<Campaign>()
                .eq(Campaign::getStatus, "active")
                .le(Campaign::getStartTime, LocalDateTime.now())
                .ge(Campaign::getEndTime, LocalDateTime.now())
                .orderByDesc(Campaign::getCreatedAt));
    }

    @Override
    @Cacheable(value = "campaign", key = "#id")
    public Campaign getById(Long id) {
        return super.getById(id);
    }
}
