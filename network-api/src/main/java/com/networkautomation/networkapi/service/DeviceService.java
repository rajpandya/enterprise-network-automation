package com.networkautomation.networkapi.service;

import com.networkautomation.networkapi.model.Device;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final List<Device> devices = List.of(
            new Device("router-prod-01", "10.10.20.15", "ONLINE", 14, "Router"),
            new Device("switch-prod-01", "10.10.20.20", "ONLINE", 8, "Switch"),
            new Device("f5-prod-01", "10.10.20.25", "WARNING", 42, "Load Balancer"),
            new Device("router-dev-01", "10.10.30.10", "OFFLINE", 0, "Router")
    );

    public Device getDeviceByIp(String ip) {
        return devices.get(0);
    }
}