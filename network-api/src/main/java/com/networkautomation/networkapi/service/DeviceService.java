package com.networkautomation.networkapi.service;

import com.networkautomation.networkapi.model.Device;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    public Device getDeviceByIp(String ip) {

        return new Device(
                "router-prod-01",
                ip,
                "ONLINE",
                14,
                "Router"
        );
    }
}