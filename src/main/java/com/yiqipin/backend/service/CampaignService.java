package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiqipin.backend.entity.Campaign;

import java.util.List;

public interface CampaignService extends IService<Campaign> {

    List<Campaign> listActive();

    Campaign getById(Long id);
}
