package com.yupi.springbootinit.model.entity.SearchResult;

import lombok.Data;

/**
 * bing的搜索结果
 */
@Data
public class searchResult {
    String title;
    String titleUrl;
    String description;
    String imgUrl;
}
