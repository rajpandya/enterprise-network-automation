package com.networkautomation.networkapi;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.networkautomation.networkapi.model.Device;
import com.networkautomation.networkapi.service.DeviceHealthCheckService;
import com.networkautomation.networkapi.service.DeviceService;

import jakarta.validation.Valid;

@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:4200"
})
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    private final DeviceHealthCheckService deviceHealthCheckService;

    public DeviceController(
        DeviceService deviceService,
        DeviceHealthCheckService deviceHealthCheckService) {
        this.deviceService = deviceService;
        this.deviceHealthCheckService = deviceHealthCheckService;
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

    @GetMapping("/health-check")
    public ResponseEntity<List<Device>> checkAllDevices() {
        return ResponseEntity.ok(
            deviceHealthCheckService.checkAllDevices()
        );
    }

}