package com.networkautomation.networkapi;

import com.networkautomation.networkapi.model.Device;
import com.networkautomation.networkapi.service.DeviceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/{ip}")
    public ResponseEntity<Device> getDevice(@PathVariable String ip) {

        Device device = deviceService.getDeviceByIp(ip);

        if (device == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(device);
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody Device device) {

        Device createdDevice = deviceService.createDevice(device);

        if (createdDevice == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDevice);
    }
}