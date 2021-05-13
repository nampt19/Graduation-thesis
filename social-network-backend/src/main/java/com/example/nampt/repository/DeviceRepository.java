package com.example.nampt.repository;

import com.example.nampt.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device,Integer> {
        Device findByDeviceToken(String token);
        List<Device> findAllByUserId(int userId);
}
