package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.SearchResult.searchResult;

/**
 * @author lzy
 */
public interface searchResultService {

    Page<searchResult> getSearchTResult(String searchText,long current,long pageSize);

}
