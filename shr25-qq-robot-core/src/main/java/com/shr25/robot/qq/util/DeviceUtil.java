package com.shr25.robot.qq.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.utils.DeviceInfo;

import java.io.File;

/**
 * 机器人终端设备工具类
 *
 * @author huobing
 * @since 2020/11/4 6:11 下午
 */
@Slf4j
@UtilityClass
public class DeviceUtil {
    /**
     * 使用QQ本地设备信息
     * @param qq
     * @return
     */
    public String getDeviceInfoJson1(Long qq) {
        // 设备信息文件
        File file = new File("deviceInfo-".concat(qq.toString()).concat(".json"));
        String deviceInfoJson = null;
        if (file.exists()) {
            FileReader fileReader = new FileReader(file);
            deviceInfoJson = fileReader.readString();
        } else {
            deviceInfoJson = getDeviceInfoJson(qq);
            log.info("重新生成设备信息:{}", deviceInfoJson);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(deviceInfoJson);
        }
        return deviceInfoJson;
    }

        /**
         * 获取机器人设备信息的JSON字符串
         *
         * @return
         */
    private String getDeviceInfoJson(Long qq) {
        String imei = "86"+(qq+"303513").substring(0, 12);
        imei = imei + luhn(imei);
        DeviceInfo deviceInfo = new DeviceInfo(
                ("SHR25."+qq.toString().substring(0,6)+".001").getBytes(),//display
                "shr25".getBytes(), //product
                "shr25".getBytes(), //device
                "shr25".getBytes(), //board
                "mamoe".getBytes(), //brand
                "shr25".getBytes(), //model
                "unknown".getBytes(), //bootloader
                ("mamoe/shr25/shr25:10/SHR25.200122.001/"+qq.toString().substring(0,7)+":user/release-keys").getBytes(), //fingerprint
                SecureUtil.md5(imei).toUpperCase().getBytes(), //bootId
                ("Linux version 3.0.31-"+qq.toString().substring(0,8)+ "(android-build@xxx.xxx.xxx.xxx.com)").getBytes(), //procVersion
                 new byte[0], //baseBand
                 new DeviceInfo.Version(), //version
                "T-Mobile".getBytes(), //simInfo
                "android".getBytes(), // osType
                "02:00:00:00:00:00".getBytes(), //macAddress
                "02:00:00:00:00:00".getBytes(), //wifiBSSID
                 "<unknown ssid>".getBytes(), //wifiSSID
                SecureUtil.md5().digest(imei), //  imsiMd5
                imei, //imei
                "wifi".getBytes() // apn
        );
        return new JSONObject(deviceInfo).toString();
    }

    //计算imei校验码
    private Integer luhn(String imei){
        boolean odd = false;
        Integer sum = 0;
        for (int i = 0; i < imei.length(); i++) {
            Integer temp = Integer.valueOf(imei.substring(i, i+1));
            odd = !odd;
            if (odd) {
                sum +=  temp;
            } else {
                Integer s = temp * 2;
                s = s % 10 + s / 10;
                sum += s;
            }
        }

        return (10 - sum % 10) % 10;
    };
}
