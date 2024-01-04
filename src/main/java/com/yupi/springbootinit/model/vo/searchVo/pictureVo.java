package com.yupi.springbootinit.model.vo.searchVo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Picture.Picture;
import lombok.Data;


/**
 * 图片返回类
 */

@Data
public class pictureVo {
    private String type;
    private Page<Picture> picturePage;
}
