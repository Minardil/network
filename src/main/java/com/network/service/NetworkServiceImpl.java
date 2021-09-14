package com.network.service;

import com.network.dto.DeviceDTO;
import com.network.dto.DeviceTreeNodeDTO;
import com.network.dto.mapper.DeviceTreeNodeModelDTOMapper;
import com.network.model.Device;
import com.network.model.DeviceTreeNode;
import com.network.model.DeviceType;
import com.network.repository.DeviceAlreadyExistsException;
import com.network.repository.DeviceIsNotRegisteredException;
import com.network.repository.DeviceRepository;
import inet.ipaddr.MACAddressString;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.network.dto.mapper.DeviceModelDTOMapper.Fields;
import static com.network.dto.mapper.DeviceModelDTOMapper.mapToDTO;

@Service
public class NetworkServiceImpl implements NetworkService {
    private final ReadWriteLock readWriteLock;

    //todo separate into cache
    private final Map<String, DeviceTreeNode> deviceMap;
    private final SortedSet<DeviceTreeNode> devices;
    private final DeviceRepository deviceRepository;
    //in order to not sort at each request
    private final Comparator<DeviceTreeNode> comparator = Comparator.<DeviceTreeNode>comparingInt(node -> getDeviceTypeOrder(node.getDevice().getDeviceType())).thenComparing(device -> device.getDevice().getMacAddress().toNormalizedString());

    @Autowired
    public NetworkServiceImpl(DeviceRepository deviceRepository) {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.deviceRepository = deviceRepository;
        this.deviceMap = new ConcurrentHashMap<>();
        this.devices = new ConcurrentSkipListSet<>(comparator);

        initCache();
    }

    @Override
    public Optional<DeviceDTO> getDeviceByMacAddress(@NonNull MACAddressString macAddressString) {
        var device = deviceMap.get(macAddressString.toNormalizedString());
        if (device == null) {
            return Optional.empty();
        } else {
            return Optional.of(mapToDTO(device.getDevice(), EnumSet.of(Fields.DeviceType, Fields.MacAddress)));
        }
    }

    @Override
    public DeviceDTO registerDevice(DeviceDTO deviceDTO) {
        readWriteLock.writeLock().lock();
        try {
            Device device = new Device().setDeviceType(deviceDTO.getDeviceType())
                    .setMacAddress(new MACAddressString(deviceDTO.getMacAddress()))
                    .setUplinkMacAddress(new MACAddressString(deviceDTO.getUplinkMacAddress()));

            String macAddressNormalized = device.getMacAddress().toNormalizedString();

            if (deviceMap.containsKey(macAddressNormalized)) {
                throw new DeviceAlreadyExistsException(macAddressNormalized);
            }

            DeviceTreeNode deviceNode = new DeviceTreeNode().setDevice(device);

            var parentMacAddress = device.getUplinkMacAddress();
            if (!parentMacAddress.isEmpty()) {
                attachDeviceToParent(deviceNode, parentMacAddress);
            }
            deviceRepository.addDevice(device);
            devices.add(deviceNode);
            deviceMap.put(macAddressNormalized, deviceNode);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return deviceDTO;
    }

    private void attachDeviceToParent(DeviceTreeNode deviceNode, MACAddressString parentMacAddress) {
        var uplinkMacAddress = parentMacAddress.toNormalizedString();
        var parentNode = deviceMap.get(uplinkMacAddress);
        if (parentNode == null) {
            throw new DeviceIsNotRegisteredException(uplinkMacAddress);
        } else {
            attachDeviceToParent(deviceNode, parentNode);
        }
    }

    private void attachDeviceToParent(DeviceTreeNode deviceNode, DeviceTreeNode parentNode) {
        SortedSet<DeviceTreeNode> parentNodes = parentNode.getNodes();
        if (parentNodes == null) {
            parentNodes = createNodeListSet();
            parentNode.setNodes(parentNodes);
        }
        parentNodes.add(deviceNode);
    }


    @Override
    public Collection<DeviceDTO> listDevices() {
        readWriteLock.readLock().lock();
        try {
            return devices.stream()
                    .map(DeviceTreeNode::getDevice)
                    .map(device -> mapToDTO(device, EnumSet.of(Fields.DeviceType, Fields.MacAddress)))
                    .collect(Collectors.toUnmodifiableList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Collection<DeviceTreeNodeDTO> getDevicesTree() {
        readWriteLock.readLock().lock();
        try {
            var filterDevices = devices.stream()
                    .filter(node -> node.getDevice().getUplinkMacAddress() == null || node.getDevice().getUplinkMacAddress().isEmpty())
                    .collect(Collectors.toUnmodifiableList());

            return DeviceTreeNodeModelDTOMapper.mapToDTOs(filterDevices, EnumSet.of(Fields.MacAddress));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<DeviceTreeNodeDTO> getDevicesSubTree(@NonNull MACAddressString macAddressString) {
        readWriteLock.readLock().lock();
        try {
            DeviceTreeNode deviceNode = deviceMap.get(macAddressString.toNormalizedString());
            if (deviceNode != null) {
                return Optional.of(DeviceTreeNodeModelDTOMapper.mapToDTO(deviceNode, EnumSet.of(Fields.MacAddress)));
            } else {
                return Optional.empty();
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private SortedSet<DeviceTreeNode> createNodeListSet() {
        return new ConcurrentSkipListSet<>(comparator);
    }

    public static int getDeviceTypeOrder(DeviceType deviceType) {
        switch (deviceType) {
            case Gateway: return 0;
            case Switch: return 1;
            case Access: return 2;
            default: throw new IllegalArgumentException("Unsupported type: " + deviceType);
        }
    }

    private void initCache() {
        readWriteLock.writeLock().lock();
        try {
            Set<Device> devicesFromRepository = deviceRepository.getAllDevices();
            devicesFromRepository.forEach(this::addDeviceToCache);
            devicesFromRepository.forEach(this::attachDeviceToParent);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void attachDeviceToParent(Device device) {
        MACAddressString uplinkMacAddress = device.getUplinkMacAddress();
        if (uplinkMacAddress != null && !uplinkMacAddress.isEmpty()) {
            DeviceTreeNode parentNode = deviceMap.get(uplinkMacAddress.toNormalizedString());
            parentNode.getNodes().add(deviceMap.get(device.getMacAddress().toNormalizedString()));
        }
    }

    private void addDeviceToCache(Device device) {
        var deviceNode = new DeviceTreeNode()
                .setDevice(device)
                .setNodes(createNodeListSet());
        deviceMap.put(device.getMacAddress().toNormalizedString(), deviceNode);
        devices.add(deviceNode);
    }
}
