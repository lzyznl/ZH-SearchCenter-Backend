package com.yupi.springbootinit.Test;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.SearchResult.searchResult;
import com.yupi.springbootinit.model.entity.Picture.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.Video;
import com.yupi.springbootinit.model.vo.searchVo.searchVo;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.searchAllService;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * 爬虫测试类
 */
@SpringBootTest
public class reptileTest {

    @Resource
    private PostService postService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private searchAllService searchAllService;


    private static final int coreNum = 20;
    private static final int maxNum = 20;
    private static final int queue_num = 10;
    public String baseKey = "searchAllResult";
    public static List<String> hotDataList = new ArrayList<>();

    @Test
    void fetchWeiBoData() throws IOException {
        String url = "https://top.baidu.com/board";
        Document doc = Jsoup.connect(url).get();
        Elements dataElement = doc.select(".c-single-text-ellipsis");
        for(int i=0;i<12;i+=2){
            String text = dataElement.get(i).text();
            hotDataList.add(text);
        }
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(queue_num);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreNum, maxNum, 3, TimeUnit.SECONDS, queue);
        ArrayList<Runnable> runnableArrayList = new ArrayList<>();
        for(int i = 0;i<hotDataList.size();++i){
            //获取到每一个热点关键词
            String hotText = hotDataList.get(i);
            //根据该热点关键词，利用线程池分别搜索出五页的内容
            for(int j = 1; j<=5;++j){
                int finalJ = j;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("开始执行");
                        searchVo searchVo = searchAllService.searchAll(hotText, finalJ, 10);
                        //将查取出来的数据存储到redis当中
                        String searchVoJson = JSONUtil.toJsonStr(searchVo);
                        String key = baseKey+"-"+hotText+"-"+finalJ;
                        stringRedisTemplate.opsForValue().set(key,searchVoJson,30,TimeUnit.MINUTES);
                        System.out.println("结束执行");
                    }
                };
                runnableArrayList.add(runnable);
            }
        }
        for(int i=0;i<runnableArrayList.size();++i){
            //执行任务列表中的任务
            threadPoolExecutor.execute(runnableArrayList.get(i));
        }
        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void fetchBingData(){
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        //System.setProperty("webdriver.chrome.driver", "D:\\chrome\\edgedriver_win64\\msedgedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 启用无头模式
        WebDriver driver = new ChromeDriver(options);
        String url = "https://www.bing.com/?toWww=1&redig=067AB329E7F54B8A94BA1174C4A37A6B";
        driver.get(url);

    }


    @Test
    void fetchVideo(){
        String searchType = "video";
        String keyword = "美食";
        String page = "3";
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
            video.setPic("Https:"+fetchVideo.getStr("pic"));
            videoList.add(video);
        }
        System.out.println(videoList);
    }

    @Test
    void fetchBingSearch() throws IOException {
        int current = 1;
        Map<String,String> urlMap = new HashMap<>();
        if(current==1){
            String url = "https://www.baidu.com/s?wd=美食&rsv_spt=1&rsv_iqid=0xaf7cd4cd0004e522&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_dl=tb&rsv_enter=1&rsv_sug3=20&rsv_sug1=12&rsv_sug7=100&rsv_sug2=0&rsv_btype=i&prefixsug=%25E5%258D%2597%25E4%25BA%25AC&rsp=9&inputT=4582&rsv_sug4=5280";
            Connection connect = Jsoup.connect(url);
            connect.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            Document document = connect.get();
            Elements pageNumElement = document.select(".page_2muyV");
            Elements aElement = pageNumElement.select("a");
            for(int i=0;i<aElement.size();++i){
                String href = aElement.get(i).attr("href");
                String num = aElement.get(i).select("span").text();
                String nextPageUrl = "https://baidu.com"+href;
                urlMap.put(num,nextPageUrl);
            }
            //获取到第一页的内容
            Elements container = document.select(".c-container");
            for(int i=0;i<container.size();++i){
                Elements containerAElement = container.get(i).select("a");
                String TitleHref = containerAElement.attr("href");
                String title = containerAElement.text();
                Elements imageElement = container.get(i).select(".c-span3");
                String imageUrl = null;
                imageUrl=imageElement.select("img").attr("src");
                Elements descriptionElement = container.get(i).select(".content-right_8Zs40");
                String description = null;
                description = descriptionElement.text();
                System.out.println(title);
                System.out.println(TitleHref);
                System.out.println(imageUrl);
                System.out.println(description);
                System.out.println("aaaaa");
            }
        }
        else if(current>1&&current<=10){

        }
    }

    @Test
    void test1(){
        System.out.println("aaaaa");
    }

    /**
     * 爬取bing的搜索结果
     * @throws IOException
     */
    @Test
    void fetchBing() throws IOException {
        String url ="https://www.bing.com/search?q=美食&sp=-1&ghc=1&lq=0&pq=da%27xue&sc=8-6&sk=&cvid=F51362D7375C4D5AA63D3195F4C13E1F&ghsh=0&ghacc=0&ghpl=&FPIG=6CC83AA1F88D4FA0A8016815638181E3&first=20&FORM=PERE1";
        Document doc = Jsoup.connect(url).get();
        Elements totalElement = doc.select(".b_algo");
        List<searchResult> searchResults = new ArrayList<>();
        for(int i=0;i<totalElement.size();++i){
            Element element = totalElement.get(i);
            Elements h2Element = element.select("h2");
            Elements aElement = h2Element.select("a");
            String href = aElement.attr("href");
            String title = aElement.text();
            Elements urlElement = element.select(".b_attribution");
            Elements cite = urlElement.select("cite");
            String wUrl = cite.text();
            Elements pElement = element.select("p");
            String description = pElement.text();
            Elements imgElement = element.select("img");
            String imgSrc = imgElement.attr("src");

            System.out.println(title);
            System.out.println(href);
            System.out.println(wUrl);
            System.out.println(description);

            searchResult searchResult = new searchResult();
            searchResult.setTitle(title);
            searchResult.setDescription(description);
            searchResult.setImgUrl(imgSrc);
            searchResults.add(searchResult);
        }
        System.out.println(totalElement);
    }


    /**
     * 抓取图片数据
     * @throws IOException
     */
    @Test
    void fetchPicture() throws IOException {
        String url = "https://www.bing.com/images/search?q=美食&qs=n&form=QBIR&sp=-1&lq=0&pq=mei&sc=10-3&cvid=E943ECD804B14790ADBCF907AD2571E5&ghsh=0&ghacc=0&first=1&cw=1177&ch=705";
        Document doc = Jsoup.connect(url).get();
        Elements newsHeadlines = doc.select(".iusc");
        List<Picture> PictureList = new ArrayList<>();
        for(int i=0;i<newsHeadlines.size();++i){
            String m = newsHeadlines.get(i).attr("m");
            Map<String,Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            String title =(String) map.get("t");
            Picture picture = new Picture();
            picture.setPictureUrl(murl);
            picture.setPictureTitle(title);
            System.out.println(murl);
            System.out.println(title);
            PictureList.add(picture);
        }
    }

    @Test
    void test(){
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
        Map<String,Object>  map = JSONUtil.toBean(result, Map.class);
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
        System.out.println(fact);
    }
}
