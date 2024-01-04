package com.yupi.springbootinit.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Video;
import com.yupi.springbootinit.service.VideoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {
    @Override
    public Page<Video> getVideoInfo(String searchText, int current, int pageSize) {
        Page<Video> videoPage = new Page<>(current,20);
        String searchType = "video";
        String keyword = searchText;
        String page = String.valueOf(current);
        String url = "https://www.bilibili.com/";
        String url2 = "https://api.bilibili.com/x/web-interface/search/type?search_type="+searchType+"&keyword="+keyword+"&page="+page;
        HttpCookie cookie = HttpRequest.get(url).execute().getCookie("buvid3");

        String body = HttpRequest.get(url2).cookie(cookie).execute().body();
        if(body==null|| StringUtils.isBlank(body)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        Map<String,Object> map = JSONUtil.toBean(body, Map.class);
        Map<String,Object> dataMap = (Map<String, Object>) map.get("data");
        JSONArray result = (JSONArray)dataMap.get("result");
        List<Video> videoList = new ArrayList<>();
        for(int i=0;i<result.size();++i){
            Video video = new Video();
            JSONObject fetchVideo = (JSONObject) result.get(i);
            video.setDescription(fetchVideo.getStr("description"));
            video.setTitle(fetchVideo.getStr("title"));
            video.setArcUrl(fetchVideo.getStr("arcurl"));
            video.setAuthor(fetchVideo.getStr("author"));
            video.setDuration(fetchVideo.getStr("duration"));
            video.setFavorites(fetchVideo.getStr("favorites"));
            video.setPlay(fetchVideo.getStr("play"));
            video.setPic("https:"+fetchVideo.getStr("pic"));
            videoList.add(video);
        }
        videoPage.setRecords(videoList);
        return videoPage;
    }
}
