package com.shr25.robot.api;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Multimap;
import com.shr25.robot.qq.model.QqMessage;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import okhttp3.Headers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MiYouSheApi implements AbstractApiMessage {


    private static String cosUrl = "https://bbs-api.miyoushe.com/post/wapi/getPostFull?gids=2&post_id=%s&read=1";

    private static String listUrl = "https://bbs-api.miyoushe.com/post/wapi/getForumPostList?forum_id=49&gids=2&is_good=false&is_hot=false&last_id=1686647616.739053&page_size=20&sort_type=1";

    @Override
    public Multimap<Contact, Message> handleMessageEvent(QqMessage qqMessage) {
        getImageUrlList();

        return null;
    }

    private static List<String> getImageUrlList() {
        List<String> urlList = new ArrayList<>();

        String res = HttpUtil.get(listUrl);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray list = jsonObject.getJSONObject("data").getJSONArray("list");

        for (Object o : list) {
            String contentRes = HttpUtil.get(String.format(cosUrl, ((JSONObject) o).getJSONObject("post").getString("post_id")));
            JSONObject contentObject = JSONObject.parseObject(contentRes);

            JSONArray jsonArray = contentObject.getJSONObject("data").getJSONObject("post").getJSONArray("image_list");
            for (Object con : jsonArray) {
                JSONObject image = (JSONObject) con;
                String url = image.getString("url");
                urlList.add(url);
            }
        }

        return urlList;
    }

    public static void main(String[] args) throws IOException {
/*        List<String> imageUrlList = getImageUrlList();
        imageUrlList.forEach(System.out::println);*/

        String contentRes = HttpUtil.post(String.format(cosUrl, "40253292"), getHeaders());
        System.out.println(contentRes);

    }

    //包装请求头
    public static Map<String, Object> getHeaders() {
        Map<String, Object> map = new HashMap<>();
        map.put("DS", getDS());
        map.put("Host", "bbs-api.miyoushe.com");
        map.put("Connection", "keep-alive");
        map.put("Cookie", "_MHYUUID=2479d8b4-976e-4e84-8b78-ede445464bb2; DEVICEFP_SEED_ID=28cea9f320615c12; DEVICEFP_SEED_TIME=1686639373472; DEVICEFP=38d7eed5569c5; _ga=GA1.1.998503508.1686639375; _ga_KS4J8TXSHQ=GS1.1.1686725724.7.0.1686725724.0.0.0; acw_tc=2f6fc10d16867257261752371efe13b8dd4fbdfc503d5754dee942c197775b");
        map.put("sec-ch-ua-mobile", "?0");
        map.put("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Microsoft Edge\";v=\"114\"");
        map.put("x-rpc-app_version", "2.52.1");
        map.put("x-rpc-client_type", "4");
        map.put("Referer", "https://www.miyoushe.com/");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("Accept", "application/json, text/plain, */*");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.43");
        map.put("x-rpc-device_id", "78ed23d51c2fac21eb2d16d828b77060");
        return map;
    }

    private static String DSGet() {

        return "1686722996,NDbiH6,a512ce25e7e2e72a91e24a7263a72579";
    }

    //生成DS
    public static String getDS() {
        String n = "z8DRIUjNDT7IT5IZXvrUAxyupA1peND9";
        String i = String.valueOf(System.currentTimeMillis() / 1000);
        String r = randomStr(6);
        String c = md5("salt=" + n + "&t=" + i + "&r=" + r);
        return i + "," + r + "," + c;
    }

    private static String randomStr(int n) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, n);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
