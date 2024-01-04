package com.yupi.springbootinit.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.picture.pictureRequest;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.search;
import com.yupi.springbootinit.model.dto.searchResult.searchResultRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.SearchResult.searchResult;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.model.vo.searchVo.*;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.service.searchResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

import static com.yupi.springbootinit.constant.searchType.*;
import static com.yupi.springbootinit.constant.searchType.pictureType;


/**
 * 搜索结果接口
 */
@RestController
@RequestMapping("/searchResult")
@Slf4j
public class SearchResultController {

    @Resource
    private searchResultService searchResultService;



    @PostMapping("/list/page/vo")
    public BaseResponse<Page<searchResult>> listMyPostVOByPage(@RequestBody searchResultRequest searchResultRequest,
                                                          HttpServletRequest request) {
        if (searchResultRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = searchResultRequest.getCurrent();
        long size = searchResultRequest.getPageSize();
        String searchText = searchResultRequest.getSearchText();
        Page<searchResult> searchTResult = searchResultService.getSearchTResult(searchText, current, size);
        return ResultUtils.success(searchTResult);
    }

}
