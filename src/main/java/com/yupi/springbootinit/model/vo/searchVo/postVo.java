package com.yupi.springbootinit.model.vo.searchVo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.vo.PostVO;
import lombok.Data;


/**
 * 帖子返回类
 */

@Data
public class postVo {
    private String type;
    private Page<PostVO> postPage;
}
