package com.networkautomation.networkapi.model;

public class Device {

    private String hostname;
    private String ip;
    private String status;
    private int latency;
    private String deviceType;

    public Device(String hostname, String ip, String status, int latency, String deviceType) {
        this.hostname = hostname;
        this.ip = ip;
        this.status = status;
        this.latency = latency;
        this.deviceType = deviceType;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public String getStatus() {
        return status;
    }

    public int getLatency() {
        return latency;
    }

    public String getDeviceType() {
        return deviceType;
    }
}