package com.network.repository;

import com.network.model.Device;
import lombok.NonNull;

import java.util.Set;

public interface DeviceRepository {
    Device addDevice(@NonNull Device device);
    Set<Device> getAllDevices();
}
