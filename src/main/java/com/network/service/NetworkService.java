package com.network.service;

import com.network.dto.DeviceDTO;
import com.network.dto.DeviceTreeNodeDTO;
import inet.ipaddr.MACAddressString;
import lombok.NonNull;

import java.util.Collection;
import java.util.Optional;

public interface NetworkService {
    Optional<DeviceDTO> getDeviceByMacAddress(MACAddressString macAddressString);
    DeviceDTO registerDevice(@NonNull DeviceDTO deviceDTO);
    Collection<DeviceDTO> listDevices();
    Collection<DeviceTreeNodeDTO> getDevicesTree();
    Optional<DeviceTreeNodeDTO> getDevicesSubTree(@NonNull MACAddressString macAddressString);
}
