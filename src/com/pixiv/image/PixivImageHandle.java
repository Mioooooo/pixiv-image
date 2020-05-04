package com.pixiv.image;

import com.pixiv.image.Json.PixivImageJson;
import com.pixiv.image.Json.PixivImageJsonRequest;
import com.pixiv.image.Json.PixivImageJsonResponse;
import com.pixiv.image.image.PixivImageRequest;
import com.pixiv.util.HttpRequestUtil;
import com.pixiv.util.JsonUtils;
import com.pixiv.util.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CA FE BA BE
 * @email ywj1134915444@Gmail.com
 * @description: PixivImageHandle
 * @compyight: Compyight (c) 2020

 * @projectName pixiv_image
 * @date 2020/5/3 18:43
 */
public class PixivImageHandle {

    public static PixivImageJsonResponse getImageJson(PixivImageJsonRequest request){
        Map<String,Object> param = new HashMap<>();
        param.put("mode",request.getMode());
        param.put("content",request.getContent());
        param.put("p",request.getP());
        param.put("date",request.getDate());
        param.put("format", request.getFormat());
        Response response = HttpRequestUtil.doGetSSL(request.getUrl(),param);
        return JsonUtils.getObject4JsonString(response.getData().toString(), PixivImageJsonResponse.class);
    }


    public static Response getImageBytes(PixivImageRequest pixivImageRequest){
        return HttpRequestUtil.doGetSSL(pixivImageRequest.getUrl(),null,pixivImageRequest);
    }
}
