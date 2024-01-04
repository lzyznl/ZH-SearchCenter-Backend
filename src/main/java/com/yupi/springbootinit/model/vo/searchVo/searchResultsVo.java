package com.yupi.springbootinit.model.vo.searchVo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.SearchResult.searchResult;
import lombok.Data;

/**
 * 网页搜索结果返回类
 * @author lzy
 */

@Data
public class searchResultsVo {
    private String type;
    private Page<searchResult> searchResultPage;
}
