package com.networkautomation.networkapi.service;

import com.networkautomation.networkapi.exception.DeviceNotFoundException;
import com.networkautomation.networkapi.exception.DuplicateDeviceException;
import com.networkautomation.networkapi.model.Device;
import com.networkautomation.networkapi.repository.DeviceRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceByIp(String ip) {
        return deviceRepository.findById(ip)
            .orElseThrow(() -> new DeviceNotFoundException(ip));
    }

    public Device createDevice(Device device) {

        if (deviceRepository.existsById(device.getIp())) {
            throw new DuplicateDeviceException(device.getIp());
        }

        return deviceRepository.save(device);
    }

    public Device updateDevice(String ip, Device updatedDevice) {

        Device existingDevice = getDeviceByIp(ip);

        existingDevice.setHostname(updatedDevice.getHostname());
        existingDevice.setStatus(updatedDevice.getStatus());
        existingDevice.setLatency(updatedDevice.getLatency());
        existingDevice.setDeviceType(updatedDevice.getDeviceType());

        return deviceRepository.save(existingDevice);
    }

    public Device patchDevice(String ip, Map<String, Object> updates) {

        Device existingDevice = getDeviceByIp(ip);

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

        return deviceRepository.save(existingDevice);
    }

    public void deleteDevice(String ip) {

        Device existingDevice = getDeviceByIp(ip);

        deviceRepository.delete(existingDevice);
    }
}