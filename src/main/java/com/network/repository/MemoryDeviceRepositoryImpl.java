package com.network.repository;

import com.network.model.Device;
import lombok.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MemoryDeviceRepositoryImpl implements DeviceRepository{
    private final ReadWriteLock readWriteLock;
    private final Set<Device> devices;
    private final Map<String, Device> deviceMap;

    public MemoryDeviceRepositoryImpl() {
        this(null);
        //todo implement loading from file
    }

    //for integration tests
    public MemoryDeviceRepositoryImpl(Collection<Device> initialData) {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.devices = new HashSet<>();
        this.deviceMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(initialData)) {
            initialData.forEach(this::addDevice);
        }
    }

    @Override
    public Device addDevice(@NonNull Device device) throws DeviceAlreadyExistsException {
        readWriteLock.writeLock().lock();
        try {
            String macAddress = device.getMacAddress().toNormalizedString();
            if (deviceMap.containsKey(macAddress)) {
                throw new DeviceAlreadyExistsException(macAddress);
            }
            devices.add(device);
            deviceMap.put(macAddress, device);
            //todo implement storing in some file
            return device;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Set<Device> getAllDevices() {
        readWriteLock.readLock().lock();
        try {
            return Set.copyOf(devices);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
