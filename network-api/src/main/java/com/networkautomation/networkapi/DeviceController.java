package com.networkautomation.networkapi;

import com.networkautomation.networkapi.model.Device;
import com.networkautomation.networkapi.service.DeviceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/{ip}")
    public ResponseEntity<Device> getDevice(@PathVariable String ip) {

        Device device = deviceService.getDeviceByIp(ip);

        if (device == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(device);
    }
}