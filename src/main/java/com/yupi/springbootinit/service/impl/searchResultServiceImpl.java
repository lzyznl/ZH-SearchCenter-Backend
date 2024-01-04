package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.SearchResult.searchResult;
import com.yupi.springbootinit.service.searchResultService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class searchResultServiceImpl implements searchResultService {

    private static Map<String,String> pageUrlMap = new HashMap<>();

    @Override
    public Page<searchResult> getSearchTResult(String searchText, long current, long pageSize) {
        Page<searchResult> page = new Page<>(current,20);
        if(current==1){
            String url = "https://www.baidu.com/s?wd="+searchText+"&rsv_spt=1&rsv_iqid=0xaf7cd4cd0004e522&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_dl=tb&rsv_enter=1&rsv_sug3=20&rsv_sug1=12&rsv_sug7=100&rsv_sug2=0&rsv_btype=i&prefixsug=%25E5%258D%2597%25E4%25BA%25AC&rsp=9&inputT=4582&rsv_sug4=5280";
            List<searchResult> dataList = getData(url,current);
            page.setRecords(dataList);
            return page;
        }
        else if(current>1&&current<=10){
            String url = pageUrlMap.get(String.valueOf(current));
            List<searchResult> dataList = getData(url,current);
            page.setRecords(dataList);
            return page;
        }else{
            return null;
        }
    }



    List<searchResult> getData(String url,long current){
        Connection connect = Jsoup.connect(url);
        connect.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        connect.cookie("BDUSS","FuWjBiYUw0ZEhCeTYtNHZjdjdWUnZBVk1OWHJtTTVhMFRrRHhxMnN-bjJpc0JrSVFBQUFBJCQAAAAAAAAAAAEAAAB34zfObHp5NjY2Njc4OWhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPb9mGT2~ZhkM3; BDUSS_BFESS=FuWjBiYUw0ZEhCeTYtNHZjdjdWUnZBVk1OWHJtTTVhMFRrRHhxMnN-bjJpc0JrSVFBQUFBJCQAAAAAAAAAAAEAAAB34zfObHp5NjY2Njc4OWhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPb9mGT2~ZhkM3; BIDUPSID=EB2786A6678EE14C77F0B7A4D1AA26DC; PSTM=1688092668; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BAIDUID=6CFDF641FE514B1BE05B842F7E090916:FG=1; BAIDUID_BFESS=6CFDF641FE514B1BE05B842F7E090916:FG=1; BA_HECTOR=85agaha5052ga0aha4200l2u1i9teke1o; ZFY=EMga6qv198yN2iZ0nMsWPj:AMDEeTJOSsNuGew1z9c18:C; delPer=0; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; PSINO=5; H_PS_PSSID=36557_38860_38798_38957_38954_38985_38919_38973_38808_38988_38638_26350");
        Document document = null;
        try {
            document = connect.get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<searchResult> searchResultList = new ArrayList<>();
        if(current==1) {
            pageUrlMap.clear();
            Elements pageNumElement = document.select(".page-inner_2jZi2");
            Elements aElement = pageNumElement.select("a");
            for (int i = 0; i < aElement.size(); i += 1) {
                String href = aElement.get(i).attr("href");
                String num = aElement.get(i).select("span").text();
                String nextPageUrl = "https://baidu.com" + href;
                pageUrlMap.put(num, nextPageUrl);
            }
        }
        //获取到第一页的内容
        Elements container = document.select(".c-container");
        for(int i=0;i<container.size();i+=2){
            Elements containerAElement = container.get(i).select("a");
            String TitleHref = containerAElement.attr("href");
            String title = containerAElement.text();
            Elements imageElement = container.get(i).select(".c-span3");
            String imageUrl = null;
            imageUrl=imageElement.select("img").attr("src");
            Elements descriptionElement = container.get(i).select(".content-right_8Zs40");
            String description = null;
            description = descriptionElement.text();

            searchResult searchResult = new searchResult();
            if((!"".equals(title))&&(!"".equals(TitleHref))&&(!"".equals(description))){
                searchResult.setDescription(description);
                searchResult.setTitle(title);
                searchResult.setImgUrl(imageUrl);
                searchResult.setTitleUrl(TitleHref);
                searchResultList.add(searchResult);
            }
        }
        return searchResultList;
    }

}
