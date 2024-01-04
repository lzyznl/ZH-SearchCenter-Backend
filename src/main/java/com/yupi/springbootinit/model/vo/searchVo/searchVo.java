package com.yupi.springbootinit.model.vo.searchVo;

import lombok.Data;

/**
 * 聚合搜索接口统一返回类
 */

@Data
public class searchVo {
    private searchResultsVo searchResultsVo;
    private postVo postVo;
    private pictureVo pictureVo;
    private userVo userVo;
    private VideoVo videoVo;
}
