package com.pixiv.image.Json;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author CA FE BA BE
 * @email ywj1134915444@Gmail.com
 * @description: Pixiv
 * @compyight: Compyight (c) 2020

 * @projectName pixiv_image
 * @date 2020/5/3 18:37
 */
@Data
public class PixivImageJson {
    /**
     * 上传时间戳
     */
    private String illustUploadTimestamp;
    /**
     * url
     */
    private String url;
    /**
     *  用户id
     */
    private String userId;
    /**
     * 排名(当天)
     */
    private String rank;
    /**
     * 图片数量
     */
    private int illustPageCount;
    /**
     * 图片编号
     */
    private String illustId;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 日期
     */
    private String date;
    /**
     * tag
     */
    private List<String> tags;
    /**
     * 类型
     */
    private Map<String,String> illustContentType;

}
