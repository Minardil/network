package com.network.repository;

import com.network.model.Device;
import lombok.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//todo implement storing and loading from file
public class MemoryDeviceRepositoryImpl implements DeviceRepository{
    private final Map<String, Device> deviceMap;

    public MemoryDeviceRepositoryImpl() {
        this(null);
    }

    //for integration tests
    public MemoryDeviceRepositoryImpl(Collection<Device> initialData) {
        this.deviceMap = new ConcurrentHashMap<>();
        if (!CollectionUtils.isEmpty(initialData)) {
            initialData.forEach(this::addDevice);
        }
    }

    @Override
    public Device addDevice(@NonNull Device device) throws DeviceAlreadyExistsException {
        String macAddress = device.getMacAddress().toNormalizedString();
        if (deviceMap.containsKey(macAddress)) {
            throw new DeviceAlreadyExistsException(macAddress);
        }
        deviceMap.put(macAddress, device);
        return device;
    }

    @Override
    public Set<Device> getAllDevices() {
        return Set.copyOf(deviceMap.values());
    }
}
