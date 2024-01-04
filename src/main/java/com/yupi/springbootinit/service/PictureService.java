package com.yupi.springbootinit.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Picture.Picture;

import java.util.List;

public interface PictureService {

    /**
     * 搜索图片接口
     * @return
     */
    Page<Picture> searchPicture(String searchText,long pageNum,long pageSize);
}
