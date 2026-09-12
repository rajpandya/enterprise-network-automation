package com.networkautomation.networkapi.service;

import com.networkautomation.networkapi.exception.DeviceNotFoundException;
import com.networkautomation.networkapi.exception.DuplicateDeviceException;
import com.networkautomation.networkapi.model.Device;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                .orElseThrow(() -> new DeviceNotFoundException(ip));
    }

    public Device createDevice(Device device) {

        boolean exists = devices.stream()
            .anyMatch(existingDevice ->
                existingDevice.getIp().equals(device.getIp()));

        if (exists) {
            throw new DuplicateDeviceException(device.getIp());
        }

        devices.add(device);
        return device;
    }

    public Device updateDevice(String ip, Device updatedDevice) {

        Device existingDevice = getDeviceByIp(ip);

        if (existingDevice == null) {
            return null;
        }

        existingDevice.setHostname(updatedDevice.getHostname());
        existingDevice.setStatus(updatedDevice.getStatus());
        existingDevice.setLatency(updatedDevice.getLatency());
        existingDevice.setDeviceType(updatedDevice.getDeviceType());

        return existingDevice;
    }

    public Device patchDevice(String ip, Map<String, Object> updates) {

        Device existingDevice = getDeviceByIp(ip);

        if (existingDevice == null) {
            return null;
        }

        if (updates.containsKey("hostname")) {
            existingDevice.setHostname((String) updates.get("hostname"));
        }

        if (updates.containsKey("status")) {
            existingDevice.setStatus((String) updates.get("status"));
        }

        if (updates.containsKey("latency")) {
            existingDevice.setLatency((Integer) updates.get("latency"));
        }

        if (updates.containsKey("deviceType")) {
            existingDevice.setDeviceType((String) updates.get("deviceType"));
        }

        return existingDevice;
    }

    public void deleteDevice(String ip) {

        Device existingDevice = getDeviceByIp(ip);

        devices.remove(existingDevice);
    }
}