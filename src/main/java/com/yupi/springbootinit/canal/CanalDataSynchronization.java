package com.yupi.springbootinit.canal;


import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 使用canal进行数据库数据同步到ES
 */
@Service
public class CanalDataSynchronization {


    private static ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    public void setElasticsearchRestTemplate(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        CanalDataSynchronization.elasticsearchRestTemplate=elasticsearchRestTemplate;
    }



    public  void dataSynchronization(){
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                11111), "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            int totalEmptyCount = 120;
            while (true) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    //System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    List<CanalEntry.Entry> entries = message.getEntries();
                    //对数据进行处理
                    List<PostEsDTO> updateAfterList = process(entries);
                    //然后将数据写入ElasticSearch当中
                    insertES(updateAfterList);
                }
                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 对数据库中发生改变的数据同步到ES当中
     * @param postEsDTOList
     * @return
     */
    public static void insertES(List<PostEsDTO> postEsDTOList){
        if(CollectionUtils.isEmpty(postEsDTOList)){
            return ;
        }
        for(PostEsDTO postEsDTO:postEsDTOList){
            long id = postEsDTO.getId();
            //先判断该id对应的文档数据是否存在，如果不存在则表明是插入，否则是更新
            PostEsDTO postEsDTO1 = elasticsearchRestTemplate.get(String.valueOf(id), PostEsDTO.class);
            if(postEsDTO1==null){
                //说明没有该id对应的文档数据，则将该文档数据进行插入
                elasticsearchRestTemplate.save(postEsDTO);
            }
            else{
                Document document = Document.create();
                //说明有该id对应的文档数据，则对该文档数据进行更新
                if(postEsDTO.getContent()!=null){
                    document.put("content",postEsDTO.getContent());
                }
                if(postEsDTO.getTitle()!=null){
                    document.put("title",postEsDTO.getTitle());
                }
                if(postEsDTO.getTags()!=null){
                    document.put("tags",postEsDTO.getTags());
                }
                if(postEsDTO.getUpdateTime()!=null){
                    document.put("updateTime",postEsDTO.getUpdateTime());
                }
                if(postEsDTO.getCreateTime()!=null){
                    document.put("createTime",postEsDTO.getCreateTime());
                }
                if(postEsDTO.getFavourNum()!=null){
                    document.put("favourNum",postEsDTO.getFavourNum());
                }
                if(postEsDTO.getThumbNum()!=null){
                    document.put("thumbNum",postEsDTO.getThumbNum());
                }
                if(postEsDTO.getIsDelete()!=null){
                    document.put("isDelete",postEsDTO.getIsDelete());
                }
                if(postEsDTO.getUserId()!=null){
                    document.put("userId",postEsDTO.getUserId());
                }
                UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(id))
                        .withDocument(document)
                        .build();
                //进行更新
                UpdateResponse response = elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of("post_v1"));
            }
        }
    }

    /**
     * 提取每一行数据
     * @param entrys
     * @return
     */
    public static List<PostEsDTO> process(List<CanalEntry.Entry> entrys) {
        List<PostEsDTO> PostEsDtoList = new ArrayList<>();
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }
            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            CanalEntry.EventType eventType = rowChage.getEventType();

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.INSERT) {
                    PostEsDTO postEsDTO = dataProcess(rowData.getAfterColumnsList());
                    PostEsDtoList.add(postEsDTO);
                } else {
                    PostEsDTO postEsDTO = dataProcess(rowData.getAfterColumnsList());
                    PostEsDtoList.add(postEsDTO);
                }
            }
        }
        return PostEsDtoList;
    }

    /**
     * 对每一行数据进行处理，包装成一个对象之后返回
     * @param columns
     * @return
     */
    public static PostEsDTO dataProcess(List<CanalEntry.Column> columns){
        PostEsDTO post = new PostEsDTO();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        formatter.setTimeZone(timeZone);
        for(CanalEntry.Column column:columns){
            if("id".equals(column.getName())){
                post.setId(Long.valueOf(column.getValue()));
            }else if("title".equals(column.getName())&&column.getUpdated()){
                post.setTitle(column.getValue());
            }else if("content".equals(column.getName())&&column.getUpdated()){
                post.setContent(column.getValue());
            }else if("tags".equals(column.getName())&&column.getUpdated()){
                post.setTags(Collections.singletonList(column.getValue()));
            }else if("thumbNum".equals(column.getName())&&column.getUpdated()){
                post.setThumbNum(Integer.valueOf(column.getValue()));
            }else if("favourNum".equals(column.getName())&&column.getUpdated()){
                post.setFavourNum(Integer.valueOf(column.getValue()));
            }else if("userId".equals(column.getName())&&column.getUpdated()) {
                post.setUserId(Long.valueOf(column.getValue()));
            }else if("createTime".equals(column.getName())&&column.getUpdated()){
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(formatter.parse(column.getValue()));
                    calendar.add(Calendar.HOUR_OF_DAY, 8);
                    Date newDate = calendar.getTime();
                    post.setCreateTime(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if("updateTime".equals(column.getName())&&column.getUpdated()){
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(formatter.parse(column.getValue()));
                    calendar.add(Calendar.HOUR_OF_DAY, 8);
                    Date newDate = calendar.getTime();
                    post.setUpdateTime(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if("isDelete".equals(column.getName())&&column.getUpdated()){
                post.setIsDelete(Integer.valueOf(column.getValue()));
            }
        }
        return post;
    }
}
