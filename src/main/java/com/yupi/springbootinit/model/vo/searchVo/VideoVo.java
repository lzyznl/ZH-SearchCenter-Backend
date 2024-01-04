package com.yupi.springbootinit.model.vo.searchVo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Video;
import lombok.Data;

@Data
public class VideoVo {
    private String type;
    private Page<Video> videoPage;
}
