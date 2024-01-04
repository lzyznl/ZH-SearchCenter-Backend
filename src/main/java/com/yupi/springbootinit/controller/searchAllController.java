package com.yupi.springbootinit.controller;


import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.job.cycle.fetchHotData;

import com.yupi.springbootinit.model.dto.search.search;

import com.yupi.springbootinit.model.vo.searchVo.*;
import com.yupi.springbootinit.service.searchAllService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 搜索结果接口
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class searchAllController {


    @Resource
    private searchAllService searchAllService;

    @GetMapping("/getHotData")
    public BaseResponse<List<String>> getHotData(){
        return ResultUtils.success(fetchHotData.hotDataList);
    }


    @PostMapping("/all")
    public BaseResponse<searchVo> searchAll(@RequestBody search search) {
        if(search==null){
            return null;
        }
        String searchText = search.getSearchText();
        int current = search.getCurrent();
        int pageSize = search.getPageSize();
        if(searchText==null|| StringUtils.isBlank(searchText)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        searchVo searchVo = searchAllService.searchAll(searchText, current, pageSize);
        return ResultUtils.success(searchVo);
    }
}
