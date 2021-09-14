package com.network.service.device.cache;

import com.network.model.DeviceTreeNode;
import inet.ipaddr.MACAddressString;

import java.util.Collection;
import java.util.Optional;

public interface DeviceNodesCache {
    void addNode(DeviceTreeNode node);
    void attachNode(MACAddressString from, MACAddressString to);
    Collection<DeviceTreeNode> getAllNodes();
    Optional<DeviceTreeNode> getNode(MACAddressString macAddress);
}
