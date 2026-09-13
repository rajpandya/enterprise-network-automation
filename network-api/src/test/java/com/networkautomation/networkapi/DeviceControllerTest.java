package com.networkautomation.networkapi;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.networkautomation.networkapi.exception.DeviceNotFoundException;
import com.networkautomation.networkapi.exception.DuplicateDeviceException;
import com.networkautomation.networkapi.model.Device;
import com.networkautomation.networkapi.service.DeviceHealthCheckService;
import com.networkautomation.networkapi.service.DeviceService;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private DeviceHealthCheckService deviceHealthCheckService;

    @Test
    void shouldReturnDeviceByIp() throws Exception {

        Device device = new Device(
                "router-prod-01",
                "10.10.20.15",
                "ONLINE",
                14,
                "Router"
        );

        given(deviceService.getDeviceByIp("10.10.20.15"))
                .willReturn(device);

        mockMvc.perform(
                get("/api/devices/10.10.20.15")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hostname").value("router-prod-01"))
        .andExpect(jsonPath("$.ip").value("10.10.20.15"))
        .andExpect(jsonPath("$.status").value("ONLINE"))
        .andExpect(jsonPath("$.latency").value(14))
        .andExpect(jsonPath("$.deviceType").value("Router"));
    }

    @Test
    void shouldReturn404WhenDeviceNotFound() throws Exception {

        String ip = "10.10.99.99";

        given(deviceService.getDeviceByIp(ip))
            .willThrow(new DeviceNotFoundException(ip));

        mockMvc.perform(
            get("/api/devices/" + ip)
        )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("DEVICE_NOT_FOUND"))
            .andExpect(jsonPath("$.message")
            .value("Device not found for IP: " + ip));
    }

    @Test
    void shouldCreateDevice() throws Exception {

        Device device = new Device(
            "router-prod-02",
            "10.10.20.50",
            "ONLINE",
            11,
            "Router"
        );

        given(deviceService.createDevice(org.mockito.ArgumentMatchers.any(Device.class)))
            .willReturn(device);

        String requestJson = """
        {
          "hostname": "router-prod-02",
          "ip": "10.10.20.50",
          "status": "ONLINE",
          "latency": 11,
          "deviceType": "Router"
        }
        """;

        mockMvc.perform(
            post("/api/devices")
                .contentType(APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.hostname").value("router-prod-02"))
            .andExpect(jsonPath("$.ip").value("10.10.20.50"))
            .andExpect(jsonPath("$.status").value("ONLINE"))
            .andExpect(jsonPath("$.latency").value(11))
            .andExpect(jsonPath("$.deviceType").value("Router"));
    }

    @Test
    void shouldReturn400ForInvalidDevice() throws Exception {

        String requestJson = """
        {
          "hostname": "",
          "ip": "",
          "status": "",
          "latency": -1,
          "deviceType": ""
        }
        """;

        mockMvc.perform(
            post("/api/devices")
                .contentType(APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message")
            .value("Request contains invalid or missing fields"));
    }

    @Test
    void shouldReturn409ForDuplicateDevice() throws Exception {

        String ip = "10.10.20.15";

        given(deviceService.createDevice(
            org.mockito.ArgumentMatchers.any(Device.class)))
            .willThrow(new DuplicateDeviceException(ip));

        String requestJson = """
        {
          "hostname": "duplicate-router",
          "ip": "10.10.20.15",
          "status": "ONLINE",
          "latency": 10,
          "deviceType": "Router"
        }
        """;

        mockMvc.perform(
            post("/api/devices")
                .contentType(APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("DUPLICATE_DEVICE"))
            .andExpect(jsonPath("$.message")
            .value("Device already exists for IP: " + ip));
    }

    @Test
    void shouldUpdateDevice() throws Exception {

        String ip = "10.10.20.15";

        Device updatedDevice = new Device(
            "router-prod-01",
            ip,
            "OFFLINE",
            0,
            "Router"
        );

        given(deviceService.updateDevice(
            org.mockito.ArgumentMatchers.eq(ip),
            org.mockito.ArgumentMatchers.any(Device.class)))
            .willReturn(updatedDevice);

        String requestJson = """
        {
          "hostname": "router-prod-01",
          "ip": "10.10.20.15",
          "status": "OFFLINE",
          "latency": 0,
          "deviceType": "Router"
        }
        """;

        mockMvc.perform(
            put("/api/devices/" + ip)
                .contentType(APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hostname").value("router-prod-01"))
            .andExpect(jsonPath("$.ip").value(ip))
            .andExpect(jsonPath("$.status").value("OFFLINE"))
            .andExpect(jsonPath("$.latency").value(0))
            .andExpect(jsonPath("$.deviceType").value("Router"));
    }

    @Test
    void shouldReturn404WhenUpdatingMissingDevice() throws Exception {

        String ip = "10.10.99.99";

        given(deviceService.updateDevice(
            org.mockito.ArgumentMatchers.eq(ip),
            org.mockito.ArgumentMatchers.any(Device.class)))
            .willThrow(new DeviceNotFoundException(ip));

        String requestJson = """
        {
          "hostname": "missing-router",
          "ip": "10.10.99.99",
          "status": "ONLINE",
          "latency": 10,
          "deviceType": "Router"
        }
        """;

        mockMvc.perform(
            put("/api/devices/" + ip)
                .contentType(APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("DEVICE_NOT_FOUND"))
            .andExpect(jsonPath("$.message")
            .value("Device not found for IP: " + ip));
    }

    @Test
    void shouldPatchDevice() throws Exception {

        String ip = "10.10.20.15";

        Device patchedDevice = new Device(
            "router-prod-01",
            ip,
            "OFFLINE",
            14,
            "Router"
        );

        given(deviceService.patchDevice(
            org.mockito.ArgumentMatchers.eq(ip),
            org.mockito.ArgumentMatchers.any(Map.class)))
            .willReturn(patchedDevice);

        String requestJson = """
        {
          "status": "OFFLINE"
        }
        """;

        mockMvc.perform(
            patch("/api/devices/" + ip)
                .contentType(APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ip").value(ip))
            .andExpect(jsonPath("$.status").value("OFFLINE"))
            .andExpect(jsonPath("$.latency").value(14));
    }

    @Test
    void shouldReturn404WhenPatchingMissingDevice() throws Exception {

        String ip = "10.10.99.99";

        given(deviceService.patchDevice(
            org.mockito.ArgumentMatchers.eq(ip),
            org.mockito.ArgumentMatchers.any(Map.class)))
            .willThrow(new DeviceNotFoundException(ip));

        String requestJson = """
        {
          "status": "OFFLINE"
        }
        """;

        mockMvc.perform(
            patch("/api/devices/" + ip)
                .contentType(APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("DEVICE_NOT_FOUND"))
            .andExpect(jsonPath("$.message")
            .value("Device not found for IP: " + ip));
    }

    @Test
    void shouldDeleteDevice() throws Exception {

        String ip = "10.10.20.15";

        mockMvc.perform(
            delete("/api/devices/" + ip)
        )
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingMissingDevice() throws Exception {

        String ip = "10.10.99.99";

        doThrow(new DeviceNotFoundException(ip))
            .when(deviceService)
            .deleteDevice(ip);

        mockMvc.perform(
            delete("/api/devices/" + ip)
        )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("DEVICE_NOT_FOUND"))
            .andExpect(jsonPath("$.message")
            .value("Device not found for IP: " + ip));
    }

    @Test
    void shouldRunHealthCheck() throws Exception {

        Device device1 = new Device(
            "router-prod-01",
            "10.10.20.15",
            "OFFLINE",
            0,
            "Router"
        );

        Device device2 = new Device(
            "local-mac",
            "127.0.0.1",
            "ONLINE",
            1,
            "Local Host"
        );

        given(deviceHealthCheckService.checkAllDevices())
            .willReturn(List.of(device1, device2));

        mockMvc.perform(get("/api/devices/health-check"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].hostname")
                    .value("router-prod-01"))
            .andExpect(jsonPath("$[0].status")
                    .value("OFFLINE"))
            .andExpect(jsonPath("$[1].hostname")
                    .value("local-mac"))
            .andExpect(jsonPath("$[1].status")
                    .value("ONLINE"));
    }
}