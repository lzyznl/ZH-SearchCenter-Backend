package com.yupi.springbootinit.model.dto.search;

import lombok.Data;

/**
 * 聚合搜索统一请求接口
 */

@Data
public class search {
    private String searchText;
    private int current;
    int pageSize;
}
