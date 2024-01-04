package com.yupi.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现CommandLineRunner这个接口后，在springBoot项目启动时会执行该方法
 */
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) throws Exception {
        String json = "{\n" +
                "    \"current\": 1,\n" +
                "    \"pageSize\": 8,\n" +
                "    \"sortField\": \"createTime\",\n" +
                "    \"sortOrder\": \"descend\",\n" +
                "    \"category\": \"文章\",\n" +
                "    \"reviewStatus\": 1\n" +
                "}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest.post(url)
                .body(json)
                .execute().body();
        Map<String,Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for(Object record:records){
            JSONObject newRecord = (JSONObject) record;
            String title = (String) newRecord.get("title");
            String content = (String) newRecord.get("content");
            Post post = new Post();
            post.setTitle(title);
            post.setContent(content);
            JSONArray tags = (JSONArray) newRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            String tagStr = JSONUtil.toJsonStr(tagList);
            post.setTags(tagStr);
            post.setUserId(1666711800852271105L);
            postList.add(post);
        }
        boolean fact = postService.saveBatch(postList);
    }
}
