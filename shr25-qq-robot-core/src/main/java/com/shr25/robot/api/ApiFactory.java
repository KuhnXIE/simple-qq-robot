package com.shr25.robot.api;

import java.util.HashMap;
import java.util.Map;

public class ApiFactory {

    /**
     * 初始化菜单
     */
    private static final Map<String, String> keyword = new HashMap<>();

    static {
        keyword.put("百度百科", "百度百科");
        keyword.put("UP", "B站");
        keyword.put("番剧", "B站");
        keyword.put("风景图", "Bing");
        keyword.put("鸡汤", "鸡汤");
        keyword.put("神秘代码", "P站");
        keyword.put("美图", "P站");
        keyword.put("涩图", "P站");
        keyword.put("高清涩图", "P站");
    }

    private static BaiKeApi baiKeApi;
    private static BilibiliApi bilibiliApi;
    private static BingWallpaperAPI bingWallpaperAPI;
    private static PixivApi pixivApi;
    private static QingYunKeApi qingYunKeApi;
    private static TongZhongApi tongZhongApi;
    private static ChickenApi chickenApi;

    public static AbstractApiMessage getInstance(String instanceName) {
        if (instanceName == null) {
            return null;
        }

        if (instanceName.equalsIgnoreCase("百度百科")) {
            if (baiKeApi == null) {
                baiKeApi = new BaiKeApi();
            }
            return baiKeApi;
        } else if (instanceName.equalsIgnoreCase("B站")) {
            if (bilibiliApi == null) {
                bilibiliApi = new BilibiliApi();
            }
            return bilibiliApi;
        } else if (instanceName.equalsIgnoreCase("Bing")) {
            if (bingWallpaperAPI == null) {
                bingWallpaperAPI = new BingWallpaperAPI();
            }
            return bingWallpaperAPI;
        } else if (instanceName.equalsIgnoreCase("P站")) {
            if (pixivApi == null) {
                pixivApi = new PixivApi();
            }
            return pixivApi;
        } else if (instanceName.equalsIgnoreCase("青云客")) {
            if (qingYunKeApi == null) {
                qingYunKeApi = new QingYunKeApi();
            }
            return qingYunKeApi;
        } else if (instanceName.equalsIgnoreCase("音乐")) {
            if (tongZhongApi == null) {
                tongZhongApi = new TongZhongApi();
            }
            return tongZhongApi;
        } else if (instanceName.equalsIgnoreCase("鸡汤")) {
            if (chickenApi == null) {
                chickenApi = new ChickenApi();
            }
            return chickenApi;
        }

        return null;
    }
}
