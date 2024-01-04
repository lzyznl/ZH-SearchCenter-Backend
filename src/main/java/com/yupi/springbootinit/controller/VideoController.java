package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.Video.video;
import com.yupi.springbootinit.model.entity.Video;
import com.yupi.springbootinit.service.VideoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author lzy
 */
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private VideoService videoService;

    @PostMapping("/get")
    public Page<Video> getVideo(@RequestBody video video){
        if(video==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String searchText = video.getSearchText();
        int current = video.getCurrent();
        Page<Video> videoInfo = videoService.getVideoInfo(searchText, current, 20);
        return videoInfo;
    }
}
