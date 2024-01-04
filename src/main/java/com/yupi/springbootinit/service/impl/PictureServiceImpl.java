package com.yupi.springbootinit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Picture.Picture;
import com.yupi.springbootinit.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureServiceImpl implements PictureService {


    /**
     * 搜索图片
     * @return
     */
    @Override
    public Page<Picture> searchPicture(String searchText,long pageNum,long pageSize) {
        long current = (pageNum-1)*pageSize;
        Page<Picture> picturePage = new Page<>(pageNum,pageSize);
        if(searchText==null||searchText.equals("")){
            return picturePage;
        }
        String url = "https://www.bing.com/images/search?q="+searchText+"&qs=n&form=QBIR&sp=-1&lq=0&pq=mei&sc=10-3&cvid=E943ECD804B14790ADBCF907AD2571E5&ghsh=0&ghacc=0&first="+current+"&cw=1177&ch=705";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据获取异常");
        }
        Elements newsHeadlines = doc.select(".iusc");
        List<Picture> PictureList = new ArrayList<>();
        for(int i=0;i<newsHeadlines.size();++i){
            if(PictureList.size()>=pageSize){
                break;
            }
            String m = newsHeadlines.get(i).attr("m");
            Map<String,Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            String title =(String) map.get("t");
            Picture picture = new Picture();
            picture.setPictureUrl(murl);
            picture.setPictureTitle(title);
            PictureList.add(picture);
        }
        picturePage.setRecords(PictureList);
        return picturePage;
    }
}
