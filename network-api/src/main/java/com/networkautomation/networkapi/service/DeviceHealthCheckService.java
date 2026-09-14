package com.networkautomation.networkapi.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.networkautomation.networkapi.model.Device;

@Service
public class DeviceHealthCheckService {

        private final DeviceService deviceService;
        private final Executor executor;


        public DeviceHealthCheckService(
                DeviceService deviceService,
                Executor executor
        ) {
                this.deviceService = deviceService;
                this.executor = executor;
        }

        public List<Device> checkAllDevices() {

                List<Device> devices = deviceService.getAllDevices();

                List<CompletableFuture<Device>> futures =
                        devices.stream()
                                .map(device ->
                                        CompletableFuture.supplyAsync(
                                                () -> checkDevice(device),
                                                executor
                                        )
                                )
                                .toList();

                return futures.stream()
                        .map(CompletableFuture::join)
                        .toList();
        }

        private Device checkDevice(Device device) {

                String ip = device.getIp();

                System.out.println(
                        "Checking " + device.getHostname()
                        + " on thread "
                        + Thread.currentThread().getName()
                );

                try {
                        Process process = new ProcessBuilder(
                                "ping",
                                "-c",
                                "1",
                                ip
                        ).start();

                        boolean finished = process.waitFor(
                                5,
                                java.util.concurrent.TimeUnit.SECONDS
                        );

                        if (!finished) {
                                process.destroyForcibly();
                                device.setStatus("OFFLINE");
                                device.setLatency(0);
                                return deviceService.updateDevice(ip, device);
                        }

                        if (process.exitValue() == 0) {
                                device.setStatus("ONLINE");
                        } else {
                                device.setStatus("OFFLINE");
                                device.setLatency(0);
                        }

                        return deviceService.updateDevice(ip, device);

                }
                catch (Exception e) {

                        device.setStatus("OFFLINE");
                        device.setLatency(0);

                        return deviceService.updateDevice(ip, device);
                }
        }
}