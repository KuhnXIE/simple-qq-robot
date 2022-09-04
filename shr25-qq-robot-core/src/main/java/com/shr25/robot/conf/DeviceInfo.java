package com.shr25.robot.conf;

import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] display = "SHR25".concat(".001").getBytes();

    private byte[] product = "SHR25".getBytes();

    private byte[] device = "SHR25".getBytes();

    private byte[] board = "SHR25".getBytes();

    private byte[] brand = "SHR25".getBytes();

    private byte[] model = "SHR25".getBytes();

    private byte[] bootloader = "unknown".getBytes();

    private byte[] fingerprint = "SHR25/SHR25/SHR25:10/SHR25.200122.001/123456".concat(":user/release-keys").getBytes();

    private byte[] bootId = UUID.randomUUID().toString().toUpperCase().getBytes();

    private byte[] procVersion = "Linux version 3.0.31-12345678".concat(" (android-build@robot.qq.shr25.com)").getBytes();

    private byte[] baseBand = new byte[0];

    private Version version = new Version();

    private byte[] simInfo = "T-Mobile".getBytes();

    private byte[] osType = "android".getBytes();

    private byte[] macAddress = "02:00:00:00:00:00".getBytes();

    private byte[] wifiBSSID = "02:00:00:00:00:00".getBytes();

    private byte[] wifiSSID = "<unknown ssid>".getBytes();

    private byte[] apn = "wifi".getBytes();

    private byte[] imsiMd5 = null;

    private String imei = null;

    public DeviceInfo(Long qq) {
        this.imei = qq + "";
        this.imsiMd5 = SecureUtil.md5().digest(imei);
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    private static class Version implements Serializable {
        private byte[] incremental = "5891938".getBytes();
        private byte[] release = "10".getBytes();
        private byte[] codename = "REL".getBytes();
        private int sdk = 29;
    }
}
