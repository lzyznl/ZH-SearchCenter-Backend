package com.yupi.springbootinit.model.dto.searchResult;

import lombok.Data;

@Data
public class searchResultRequest {
    int current;
    int pageSize;
    String searchText;
}
