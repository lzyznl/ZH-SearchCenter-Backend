package com.yupi.springbootinit.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.yupi.springbootinit.model.entity.Video;


public interface VideoService {

    /**
     * 获取视频信息接口
     * @param searchText
     * @param current
     * @param pageSize
     * @return
     */
    Page<Video> getVideoInfo(String searchText,int current,int pageSize);
}
