package com.yupi.springbootinit.model.entity;

import lombok.Data;

/**
 * 视频数据
 */
@Data
public class Video {
    /**
     * 视频作者
     */
    private String author;
    /**
     * 视频重定向URL
     */
    private String arcUrl;
    /**
     * 视频标题
     */
    private String title;
    /**
     * 视频描述
     */
    private String description;
    /**
     * 视频播放量
     */
    private String play;

    /**
     * 视频收藏数
     */
    private String favorites;

    /**
     * 视频时长
     */
    private String Duration;

    /**
     * 视频封面URL
     */
    private String pic;
}
