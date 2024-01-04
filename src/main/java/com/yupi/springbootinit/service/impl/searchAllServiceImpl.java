package com.yupi.springbootinit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.yupi.springbootinit.job.cycle.fetchHotData;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture.Picture;
import com.yupi.springbootinit.model.entity.SearchResult.searchResult;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.entity.Video;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.model.vo.searchVo.*;
import com.yupi.springbootinit.service.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

import static com.yupi.springbootinit.constant.searchType.*;


@Service
public class searchAllServiceImpl implements searchAllService {

    @Resource
    private searchResultService searchResultService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;


    @Resource
    private PictureService pictureService;

    @Resource
    private VideoService videoService;

    @Override
    public searchVo searchAll(String searchText, int current, int pageSize) {

        String searchKey = fetchHotData.baseKey+"-"+searchText+"-"+current;
        String searchJson = stringRedisTemplate.opsForValue().get(searchKey);
        searchVo searchVo1 = JSONUtil.toBean(searchJson, searchVo.class);
        if(!(searchVo1.getUserVo()==null&&searchVo1.getVideoVo()==null&&searchVo1.getPostVo()==null&&searchVo1.getPictureVo()==null
                    &&searchVo1.getSearchResultsVo()==null) ){
            return searchVo1;
        }

        //查询所有数据
        CompletableFuture<Page<searchResult>> searchResultTask = CompletableFuture.supplyAsync(() -> {
            Page<searchResult> searchResultPage = searchResultService.getSearchTResult(searchText, current, pageSize);
            return searchResultPage;
        });

        CompletableFuture<Page<PostVO>> postResultTask = CompletableFuture.supplyAsync(() -> {
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            Page<PostVO> postVOPage = postService.searchFromEs(postQueryRequest);
            return postVOPage;
        });

        CompletableFuture<Page<UserVO>> userResultTask = CompletableFuture.supplyAsync(() -> {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            Page<User> userPage = userService.page(new Page<>(current, pageSize),
                    userService.getQueryWrapper(userQueryRequest));
            Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
            return userVOPage;
        });

        CompletableFuture<Page<Picture>> pictureResultTask = CompletableFuture.supplyAsync(() -> {
            Page<Picture> picturePage = pictureService.searchPicture(searchText, current, pageSize);
            return picturePage;
        });

        CompletableFuture<Page<Video>> videoTask = CompletableFuture.supplyAsync(() -> {
            Page<Video> videoInfo = videoService.getVideoInfo(searchText, current, 20);
            return videoInfo;
        });

        CompletableFuture.allOf(searchResultTask,userResultTask,pictureResultTask,videoTask,postResultTask).join();

        /*CompletableFuture.allOf(userResultTask,pictureResultTask,videoTask,postResultTask);*/
        try {
            searchVo searchVo = new searchVo();

            Page<searchResult> searchResultPage = searchResultTask.get();
            searchResultsVo searchResultsVo = new searchResultsVo();
            searchResultsVo.setSearchResultPage(searchResultPage);
            searchResultsVo.setType(searchResultType);
            searchVo.setSearchResultsVo(searchResultsVo);

            Page<PostVO> postVOPage = postResultTask.get();
            postVo postVo = new postVo();
            postVo.setPostPage(postVOPage);
            postVo.setType(postType);
            searchVo.setPostVo(postVo);

            Page<UserVO> userResultPage = userResultTask.get();
            userVo userVo = new userVo();
            userVo.setUserPage(userResultPage);
            userVo.setType(userType);
            searchVo.setUserVo(userVo);

            Page<Picture> pictureResultPage = pictureResultTask.get();
            pictureVo pictureVo = new pictureVo();
            pictureVo.setPicturePage(pictureResultPage);
            pictureVo.setType(pictureType);
            searchVo.setPictureVo(pictureVo);

            Page<Video> videoResultPage = videoTask.get();
            VideoVo videoVo = new VideoVo();
            videoVo.setType(VideoType);
            videoVo.setVideoPage(videoResultPage);
            searchVo.setVideoVo(videoVo);

            return searchVo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
