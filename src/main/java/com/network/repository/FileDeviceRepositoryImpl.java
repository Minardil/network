package com.network.repository;

import com.network.config.DeviceRepositoryProperties;
import com.network.model.Device;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FileDeviceRepositoryImpl implements DeviceRepository {
    private final Set<String> devicesIds;
    private final File file;
    private final DevicesFileReader devicesFileReader;
    private final DevicesFileWriter devicesFileWriter;

    public FileDeviceRepositoryImpl(DeviceRepositoryProperties deviceRepositoryProperties, DevicesFileReader devicesFileReader, DevicesFileWriter devicesFileWriter) {
        this.devicesFileReader = devicesFileReader;
        this.devicesFileWriter = devicesFileWriter;
        this.devicesIds = new HashSet<>();
        this.file = new File(deviceRepositoryProperties.getDbPath());
    }

    @Override
    public Device addDevice(@NonNull Device device) throws DeviceAlreadyExistsException {
        String macAddress = device.getMacAddress().toNormalizedString();
        if (devicesIds.contains(macAddress)) {
            throw new DeviceAlreadyExistsException(macAddress);
        }
        try {
            devicesFileWriter.writeDevice(file, device);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        devicesIds.add(macAddress);
        return device;
    }

    @Override
    public Collection<Device> getAllDevices() {
        try {
            Collection<Device> devices = devicesFileReader.readDevices(file);
            devices.forEach(device -> devicesIds.add(device.getMacAddress().toNormalizedString()));
            return devices;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
