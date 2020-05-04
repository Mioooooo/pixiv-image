package com.pixiv.image.image;

import lombok.Data;

/**
 * @author CA FE BA BE
 * @email ywj1134915444@Gmail.com
 * @description: Pixiv
 * @compyight: Compyight (c) 2020
 * @company: 福建索天信息科技股份有限公司
 * @projectName pixiv_image
 * @date 2020/5/3 18:37
 */
@Data
public class PixivImageRequest {
    private String url;
    private String host;
    private String referer;
    private String illustId;

}
