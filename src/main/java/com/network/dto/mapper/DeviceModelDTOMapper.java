package com.network.dto.mapper;

import com.network.dto.DeviceDTO;
import com.network.model.Device;

import java.util.Collection;
import java.util.stream.Collectors;

public class DeviceModelDTOMapper {

    public static DeviceDTO mapToDTO(Device device) {
        DeviceDTO result = new DeviceDTO();
        result.setMacAddress(device.getMacAddress().toNormalizedString());
        result.setDeviceType(device.getDeviceType());
        return result;
    }

    public static Collection<DeviceDTO> mapToDTOs(Collection<Device> devices) {
        return devices.stream()
                .map(DeviceModelDTOMapper::mapToDTO)
                .collect(Collectors.toUnmodifiableList());
    }
}
