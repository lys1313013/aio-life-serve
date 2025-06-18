package com.lys.datasource;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 热榜视图
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Data
@Builder
public class HotPostDataVO implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 热度
     */
    private Integer followerCount;

    /**
     * 链接
     */
    private String url;


}
