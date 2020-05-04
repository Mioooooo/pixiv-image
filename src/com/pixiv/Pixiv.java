package com.pixiv;

import com.pixiv.image.Json.PixivImageJson;
import com.pixiv.image.Json.PixivImageJsonRequest;
import com.pixiv.image.Json.PixivImageJsonResponse;
import com.pixiv.image.PixivImageHandle;
import com.pixiv.image.image.PixivImageRequest;
import com.pixiv.util.Response;
import org.apache.commons.lang.time.DateUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CA FE BA BE
 * @email ywj1134915444@Gmail.com
 * @description: Pixiv
 * @compyight: Compyight (c) 2020
 * @company: 福建索天信息科技股份有限公司
 * @projectName pixiv_image
 * @date 2020/5/3 18:34
 */
public class Pixiv {

    private static final String IMAGE_JSON_REQUEST_URL = "https://www.pixiv.net/ranking.php";
    private static final String IMAGE_JSON_REQUEST_CONTENT = "illust";
    private static final String IMAGE_JSON_REQUEST_FORMAT = "json";
    private static final String IMAGE_JSON_REQUEST_MODEL = "daily";
    private static final String IMAGE_JSON_REQUEST_P = "1";
    private static final String HOST = "i.pximg.net";
    private static final String REFERER = "https://www.pixiv.net/artworks/";


    public static void main(String[] args) throws Exception {
        String startDate = "20200501";
        String endDate = "20200504";
        while (Integer.parseInt(startDate) <= Integer.parseInt(endDate)) {
            System.out.println("开始处理日期>>>  " + startDate);
            Response response;
            PixivImageJsonRequest pixivImageJsonRequest = handlePixivImageJsonRequest(startDate);
            System.out.println("开始获取JSON的request>>>  " + pixivImageJsonRequest.toString());
            PixivImageJsonResponse pixivImageJsonResponse = new PixivImageJsonResponse();
            pixivImageJsonResponse.setNext("0");
            while (!pixivImageJsonResponse.getNext().equals("false")) {
                pixivImageJsonResponse = PixivImageHandle.getImageJson(pixivImageJsonRequest);
                System.out.println("开始处理第"+pixivImageJsonResponse.getPage()+"页的数据");
                pixivImageJsonRequest.setP(pixivImageJsonResponse.getNext());
                System.out.println("开始获取JSON的长度>>>  " + pixivImageJsonResponse.getRankTotal());
                for (PixivImageJson pixivImageJson : pixivImageJsonResponse.getContents()) {
                    pixivImageJson.setDate(startDate);
                    System.out.println("开始开始处理rank为" + pixivImageJson.getRank() + "的数据>>>  " + pixivImageJson.toString());
                    for (int i = 0; i < pixivImageJson.getIllustPageCount(); i++) {
                        System.out.println("开始下载第" + (i +1) + "张png文件");
                        pixivImageJson.setSuffix(".png");
                        PixivImageRequest pixivImageRequest = handlePixivImageJson(pixivImageJson, i);
                        response = PixivImageHandle.getImageBytes(pixivImageRequest);
                        if (isNull(response)) {
                            System.out.println("png文件下载失败,开始下载jpg文件");
                            pixivImageJson.setSuffix(".jpg");
                            pixivImageRequest = handlePixivImageJson(pixivImageJson, i);
                            response = PixivImageHandle.getImageBytes(pixivImageRequest);
                        }
                        saveFile(pixivImageJson, response, i);
                    }
                    System.out.println("文件下载成功" + pixivImageJson.toString());
                }
            }
            startDate = nextDate(startDate);
        }
    }

    private static String nextDate(String startDate) throws Exception{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(DateUtils.addDays(simpleDateFormat.parse(startDate),1));
    }

    private static boolean isNull(Response response) {
        return response.getData().toString().startsWith("-1");
    }

    private static void saveFile(PixivImageJson pixivImageJson, Response response,int i) {
        String filePath = getFilePath(pixivImageJson);
        String fileName = getFileName(pixivImageJson,i);
        File fileD = new File(filePath);
        if(!fileD.exists()){
            boolean mkdirs = fileD.mkdirs();
            if (!mkdirs) {
                System.out.println("文件夹创建失败");
            }
        }
        try{

        File file = new File(filePath+fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        boolean newFile = file.createNewFile();
        fileOutputStream.write(response.getData().toString().getBytes(StandardCharsets.ISO_8859_1));
        fileOutputStream.flush();
        fileOutputStream.close();
        System.out.println(pixivImageJson.getSuffix()+"文件下载成功");
        File fileLog = new File(filePath+"log");
        if(!fileLog.exists()){
            boolean flag = fileLog.createNewFile();
            if(!flag){
                System.out.println("日志文件创建失败");
            }
            System.out.println("日志文件创建成功");
        }
        FileWriter writer = new FileWriter(fileLog, true);
        writer.write(pixivImageJson.toString()+"\r\n");
        writer.flush();
        writer.close();
    }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getFileName(PixivImageJson pixivImageJson, int i) {
        StringBuilder fileName = new StringBuilder();
        for (String tag:pixivImageJson.getTags()){
            fileName.append(tag).append("_");
        }
        fileName.append(pixivImageJson.getUserId()).append("_").append(pixivImageJson.getIllustId()).append("_").append(pixivImageJson.getRank()).append("_p").append(i);
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(fileName);
        return m.replaceAll("").trim()+pixivImageJson.getSuffix();
    }

    private static String getFilePath(PixivImageJson pixivImageJson) {
        String filePath= "C:\\Users\\CA FE BA BE\\Desktop\\pixiv爬\\image\\" +pixivImageJson.getDate()+"\\";
        if ("0".equals(pixivImageJson.getIllustContentType().get("sexual"))){
            filePath+="ordinary//";
        }else{
            filePath+="sexual//";
        }
        if(pixivImageJson.getIllustContentType().get("lo").equals("true")){
            filePath+="lo//";
        }
        if(pixivImageJson.getIllustContentType().get("grotesque").equals("true")){
            filePath+="grotesque//";
        }
        if(pixivImageJson.getIllustContentType().get("violent").equals("true")){
            filePath+="violent//";
        }
        if(pixivImageJson.getIllustContentType().get("homosexual").equals("true")){
            filePath+="homosexual//";
        }
        if(pixivImageJson.getIllustContentType().get("drug").equals("true")){
            filePath+="drug//";
        }
        if(pixivImageJson.getIllustContentType().get("thoughts").equals("true")){
            filePath+="thoughts//";
        }
        if(pixivImageJson.getIllustContentType().get("antisocial").equals("true")){
            filePath+="antisocial//";
        }
        if(pixivImageJson.getIllustContentType().get("religion").equals("true")){
            filePath+="religion//";
        }
        if(pixivImageJson.getIllustContentType().get("original").equals("true")){
            filePath+="original//";
        }
        if(pixivImageJson.getIllustContentType().get("furry").equals("true")){
            filePath+="furry//";
        }
        if(pixivImageJson.getIllustContentType().get("bl").equals("true")){
            filePath+="bl//";
        }
        if(pixivImageJson.getIllustContentType().get("yuri").equals("true")){
            filePath+="yuri//";
        }
        return filePath;
    }

    private static PixivImageRequest handlePixivImageJson(PixivImageJson pixivImageJson, int i) {
        String url = "https://i.pximg.net/img-original/img/";
        String date = pixivImageJson.getIllustUploadTimestamp() + "000";
        String illustId = pixivImageJson.getIllustId();
        date = stampToDate(date);
        url += date + illustId + "_p"+i+pixivImageJson.getSuffix();
        PixivImageRequest pixivImageRequest = new PixivImageRequest();
        pixivImageRequest.setUrl(url);
        pixivImageRequest.setIllustId(illustId);
        pixivImageRequest.setHost(HOST);
        pixivImageRequest.setReferer(REFERER);
        return pixivImageRequest;
    }

    private static PixivImageJsonRequest handlePixivImageJsonRequest(String date) {
        PixivImageJsonRequest pixivImageJsonRequest = new PixivImageJsonRequest();
        pixivImageJsonRequest.setUrl(IMAGE_JSON_REQUEST_URL);
        pixivImageJsonRequest.setDate(date);
        pixivImageJsonRequest.setContent(IMAGE_JSON_REQUEST_CONTENT);
        pixivImageJsonRequest.setFormat(IMAGE_JSON_REQUEST_FORMAT);
        pixivImageJsonRequest.setP(IMAGE_JSON_REQUEST_P);
        pixivImageJsonRequest.setMode(IMAGE_JSON_REQUEST_MODEL);
        return pixivImageJsonRequest;
    }
    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss/");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
}
