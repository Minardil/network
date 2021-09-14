package com.network.service.device;

import com.network.dto.DeviceDTO;
import com.network.dto.DeviceTreeNodeDTO;
import com.network.dto.mapper.DeviceModelDTOMapper;
import com.network.dto.mapper.DeviceTreeNodeModelDTOMapper;
import com.network.model.Device;
import com.network.model.DeviceTreeNode;
import com.network.repository.DeviceAlreadyExistsException;
import com.network.repository.DeviceIsNotRegisteredException;
import com.network.repository.DeviceRepository;
import com.network.service.device.cache.DeviceNodesCache;
import inet.ipaddr.MACAddressString;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.network.dto.mapper.DeviceModelDTOMapper.mapToDTO;

@Service
public class DevicesServiceImpl implements DevicesService {
    private final ReadWriteLock readWriteLock;

    private final DeviceRepository deviceRepository;
    private final DeviceNodesCache deviceNodesCache;

    @Autowired
    public DevicesServiceImpl(DeviceRepository deviceRepository, DeviceNodesCache deviceNodesCache) {
        this.deviceNodesCache = deviceNodesCache;
        this.readWriteLock = new ReentrantReadWriteLock();
        this.deviceRepository = deviceRepository;

        initCache();
    }

    @Override
    public Optional<DeviceDTO> getDeviceByMacAddress(@NonNull MACAddressString macAddressString) {
        readWriteLock.readLock().lock();
        try {
            var device = deviceNodesCache.getNode(macAddressString);
            if (device.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(mapToDTO(device.get().getDevice()));
            }
        } finally {
            readWriteLock.readLock().unlock();
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

            Optional<DeviceTreeNode> node = deviceNodesCache.getNode(device.getMacAddress());

            if (node.isPresent()) {
                throw new DeviceAlreadyExistsException(macAddressNormalized);
            }

            DeviceTreeNode deviceNode = new DeviceTreeNode().setDevice(device);

            var parentMacAddress = device.getUplinkMacAddress();
            if (!parentMacAddress.isEmpty()) {
                validateParent(parentMacAddress);
            }

            deviceRepository.addDevice(device);

            deviceNodesCache.addNode(deviceNode);
            if (!parentMacAddress.isEmpty()) {
                deviceNodesCache.attachNode(deviceNode.getDevice().getMacAddress(), parentMacAddress);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return deviceDTO;
    }

    private void validateParent(MACAddressString parentMacAddress) throws DeviceIsNotRegisteredException {
        var uplinkMacAddress = parentMacAddress.toNormalizedString();
        var parentNode = deviceNodesCache.getNode(parentMacAddress);
        if (parentNode.isEmpty()) {
            throw new DeviceIsNotRegisteredException(uplinkMacAddress);
        }
    }

    @Override
    public Collection<DeviceDTO> listDevices() {
        readWriteLock.readLock().lock();
        try {
            return deviceNodesCache.getAllNodes().stream()
                    .map(DeviceTreeNode::getDevice)
                    .map(DeviceModelDTOMapper::mapToDTO)
                    .collect(Collectors.toUnmodifiableList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Collection<DeviceTreeNodeDTO> getDevicesTree() {
        readWriteLock.readLock().lock();
        try {
            var filterDevices = deviceNodesCache.getAllNodes().stream()
                    .filter(node -> node.getDevice().getUplinkMacAddress() == null || node.getDevice().getUplinkMacAddress().isEmpty())
                    .collect(Collectors.toUnmodifiableList());

            return DeviceTreeNodeModelDTOMapper.mapToDTOs(filterDevices);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<DeviceTreeNodeDTO> getNode(@NonNull MACAddressString macAddressString) {
        readWriteLock.readLock().lock();
        try {
            Optional<DeviceTreeNode> deviceNode = deviceNodesCache.getNode(macAddressString);
            return deviceNode.map(DeviceTreeNodeModelDTOMapper::mapToDTO);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private void initCache() {
        readWriteLock.writeLock().lock();
        try {
            Collection<Device> devicesFromRepository = deviceRepository.getAllDevices();
            devicesFromRepository.forEach(this::addDeviceToCache);
            devicesFromRepository.forEach(this::attachNodes);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void attachNodes(Device device) {
        MACAddressString uplinkMacAddress = device.getUplinkMacAddress();
        if (uplinkMacAddress != null && !uplinkMacAddress.isEmpty()) {
            deviceNodesCache.attachNode(device.getMacAddress(), uplinkMacAddress);
        }
    }

    private void addDeviceToCache(Device device) {
        var deviceNode = new DeviceTreeNode().setDevice(device);
        deviceNodesCache.addNode(deviceNode);
    }
}
