package com.networkautomation.networkapi.service;

import com.networkautomation.networkapi.model.Device;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceService {

    private final List<Device> devices = new ArrayList<>(List.of(
            new Device("router-prod-01", "10.10.20.15", "ONLINE", 14, "Router"),
            new Device("switch-prod-01", "10.10.20.20", "ONLINE", 8, "Switch"),
            new Device("f5-prod-01", "10.10.20.25", "WARNING", 42, "Load Balancer"),
            new Device("router-dev-01", "10.10.30.10", "OFFLINE", 0, "Router")
    ));

    public List<Device> getAllDevices() {
        return devices;
    }

    public Device getDeviceByIp(String ip) {
        return devices.stream()
                .filter(device -> device.getIp().equals(ip))
                .findFirst()
                .orElse(null);
    }

    public Device createDevice(Device device) {

        if (getDeviceByIp(device.getIp()) != null) {
            return null;
        }

        devices.add(device);
        return device;
    }
}