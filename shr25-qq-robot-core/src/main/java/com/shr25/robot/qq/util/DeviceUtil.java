package com.shr25.robot.qq.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONObject;
import com.shr25.robot.conf.DeviceInfo;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * 机器人终端设备工具类
 *
 * @author huobing
 * @since 2020/11/4 6:11 下午
 */
@UtilityClass
public class DeviceUtil {

    /**
     * 获取机器人设备信息的JSON字符串
     *
     * @return
     */
    public String getDeviceInfoJson(Long qq) {
        return new JSONObject(new DeviceInfo(qq)).toString();
    }

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
            deviceInfoJson = new JSONObject().toString();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(deviceInfoJson);
        }
        return deviceInfoJson;
    }
}
