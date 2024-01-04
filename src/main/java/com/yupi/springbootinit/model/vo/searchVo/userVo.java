package com.yupi.springbootinit.model.vo.searchVo;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.UserVO;
import lombok.Data;

/**
 * 用户返回类
 * @author lzy
 */

@Data
public class userVo {
    private String type;
    private Page<UserVO> userPage;
}
