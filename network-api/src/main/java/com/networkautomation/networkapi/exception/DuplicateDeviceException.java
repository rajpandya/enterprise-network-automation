package com.networkautomation.networkapi.exception;

public class DuplicateDeviceException extends RuntimeException {

    public DuplicateDeviceException(String ip) {
        super("Device already exists for IP: " + ip);
    }
}