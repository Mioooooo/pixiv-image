package com.pixiv.image.Json;

import lombok.Data;

import java.util.List;

/** 图片请求应答
 * @author CA FE BA BE
 * @email ywj1134915444@Gmail.com
 * @description: PixivImageJsonResponse
 * @compyight: Compyight (c) 2020

 * @projectName pixiv_image
 * @date 2020/5/3 18:37
 */
@Data
public class PixivImageJsonResponse {

    private List<PixivImageJson> contents;
    /**
     * 列表模式(日,周,月)
     */
    private String mode;
    /**
     * 图片类型
     */
    private String content;
    /**
     * 当前页
     */
    private String page;
    /**
     *  上个页
     */
    private String prev;
    /**
     * 下个页数
     */
    private String next;
    /**
     * 查询图片的日期
     */
    private String date;
    /**
     * 上个日期
     */
    private String prevDate;
    /**
     *下个日期
     */
    private String nextDate;
    /**
     * 图片总数
     */
    private String rankTotal;
}
