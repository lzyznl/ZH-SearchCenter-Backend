package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.vo.searchVo.searchVo;
import org.springframework.transaction.annotation.Transactional;


public interface searchAllService {


    searchVo searchAll(String searchText,int current,int pageSize);
}
