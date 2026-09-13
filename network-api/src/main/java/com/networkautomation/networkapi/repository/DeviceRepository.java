package com.networkautomation.networkapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.networkautomation.networkapi.model.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {
}