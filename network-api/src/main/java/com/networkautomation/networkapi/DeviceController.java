package com.networkautomation.networkapi;

import com.networkautomation.networkapi.model.Device;
import com.networkautomation.networkapi.service.DeviceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

import java.util.Map;

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
        return ResponseEntity.ok(deviceService.getDeviceByIp(ip));
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody Device device) {

        Device createdDevice = deviceService.createDevice(device);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDevice);
    }

    @PutMapping("/{ip}")
    public ResponseEntity<Device> updateDevice(
        @PathVariable String ip,
        @Valid @RequestBody Device device) {

        Device updatedDevice = deviceService.updateDevice(ip, device);

        if (updatedDevice == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedDevice);
    }

    @PatchMapping("/{ip}")
    public ResponseEntity<Device> patchDevice(
        @PathVariable String ip,
        @RequestBody Map<String, Object> updates) {

        Device updatedDevice = deviceService.patchDevice(ip, updates);

        if (updatedDevice == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String ip) {

        deviceService.deleteDevice(ip);

        return ResponseEntity.noContent().build();
    }

}