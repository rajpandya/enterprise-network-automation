package com.networkautomation.networkapi.exception;

public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String ip) {
        super("Device not found for IP: " + ip);
    }
}