package com.yupi.springbootinit.job.cycle;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.vo.searchVo.searchVo;
import com.yupi.springbootinit.service.searchAllService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 获取百度热搜数据，并且将数据写入redis数据库中
 */
@Slf4j
@Configuration
@EnableScheduling
public class fetchHotData {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private searchAllService searchAllService;

    @Resource
    private RedissonClient redissonClient;

    private static final String url ="https://top.baidu.com/board";

    private static final int coreNum = 15;
    private static final int maxNum = 20;
    private static final int queue_num = 10;
    public static String baseKey = "searchAllResult";

    public static List<String> hotDataList = new ArrayList<>();

    @Scheduled(cron ="0 0/1 * * * ? ")
    @PostConstruct
    public void fetchHotData(){
        log.info("开始执行获取热搜数据的任务了");
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("爬取热搜数据失败");
        }
        hotDataList.clear();
        Elements dataElement = doc.select(".c-single-text-ellipsis");
        for(int i=0;i<12;i+=2){
            String text = dataElement.get(i).text();
            hotDataList.add(text);
        }
        //加入分布式锁，保证在集群部署时，只有一台机器可以执行一次
        RLock lock = redissonClient.getLock("getHotData:insertRedis");
        boolean tryLockFact = false;
        try {
            //设置锁的过期时间之后是不会触发看门狗机制的
            tryLockFact = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(tryLockFact) {
            //insertToRedis();
            lock.unlock();
        }
    }

    /**
     * 创建线程池，获取多个线程来执行获取热点数据的任务，获取到之后随即存入Redis当中
     */
    public void insertToRedis(){
        if(hotDataList.size()==0){
            return ;
        }
        for(int i=0;i<hotDataList.size();++i){
            String hotText = hotDataList.get(i);
            //向Redis中写入数据
            for(int j = 1;j<=6;++j){
                String key = baseKey+"-"+hotText+"-"+j;
                searchVo searchVo = searchAllService.searchAll(hotText, j, 20);
                String searchVoJson = JSONUtil.toJsonStr(searchVo);
                stringRedisTemplate.opsForValue().set(key,searchVoJson,60,TimeUnit.MINUTES);
            }
        }
    }

}
