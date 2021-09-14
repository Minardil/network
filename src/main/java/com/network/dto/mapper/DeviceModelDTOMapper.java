package com.network.dto.mapper;

import com.network.dto.DeviceDTO;
import com.network.model.Device;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class DeviceModelDTOMapper {
    public enum Fields {
        DeviceType,
        MacAddress,
        UplinkMacAddress
    }

    //todo extract field filtering to controller level
    public static DeviceDTO mapToDTO(Device device, Set<Fields> fields) {
        DeviceDTO result = new DeviceDTO();
        if (fields.contains(Fields.MacAddress)) {
            result.setMacAddress(device.getMacAddress().toNormalizedString());
        }
        if (fields.contains(Fields.UplinkMacAddress)) {
            result.setUplinkMacAddress(device.getUplinkMacAddress().toNormalizedString());
        }
        if (fields.contains(Fields.DeviceType)) {
            result.setDeviceType(device.getDeviceType());
        }
        return result;
    }

    //todo extract field filtering to controller level
    public static Collection<DeviceDTO> mapToDTOs(Collection<Device> devices, Set<Fields> fields) {
        return devices.stream()
                .map(device -> mapToDTO(device, fields))
                .collect(Collectors.toUnmodifiableList());
    }
}
