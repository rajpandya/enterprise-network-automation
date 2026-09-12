package com.networkautomation.networkapi.model;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Device {

    @NotBlank
    private String hostname;

    @NotBlank
    private String ip;

    @NotBlank
    private String status;

    @Min(0)
    private int latency;

    @NotBlank
    private String deviceType;

    public Device() {
    }

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