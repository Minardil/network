package com.network.service.device.cache;

import com.network.model.DeviceTreeNode;
import inet.ipaddr.MACAddressString;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DeviceNodesCacheImpl implements DeviceNodesCache {
    private final Map<String, DeviceTreeNode> deviceMap;
    private final SortedSet<DeviceTreeNode> devices;

    //in order not to sort later
    private final Comparator<DeviceTreeNode> comparator = Comparator.<DeviceTreeNode>comparingInt(node -> node.getDevice().getDeviceType().getOrder()).thenComparing(device -> device.getDevice().getMacAddress().toNormalizedString());

    public DeviceNodesCacheImpl() {
        devices = createNodeListSet();
        deviceMap = new HashMap<>();
    }

    @Override
    public void addNode(DeviceTreeNode node) {
        devices.add(node);
        deviceMap.put(node.getDevice().getMacAddress().toNormalizedString(), node);
    }

    @Override
    public void attachNode(MACAddressString from, MACAddressString to) {
        DeviceTreeNode parentNode = deviceMap.get(to.toNormalizedString());
        Collection<DeviceTreeNode> nodes = getOrCreateNodeList(parentNode);
        nodes.add(deviceMap.get(from.toNormalizedString()));
    }

    private Collection<DeviceTreeNode> getOrCreateNodeList(DeviceTreeNode parentNode) {
        Collection<DeviceTreeNode> nodes = parentNode.getNodes();
        if (nodes == null) {
            nodes = createNodeListSet();
            parentNode.setNodes(nodes);
        }
        return nodes;
    }

    private SortedSet<DeviceTreeNode> createNodeListSet() {
        return new TreeSet<>(comparator);
    }

    @Override
    public Collection<DeviceTreeNode> getAllNodes() {
        return devices;
    }

    @Override
    public Optional<DeviceTreeNode> getNode(MACAddressString macAddress) {
        DeviceTreeNode node = deviceMap.get(macAddress.toNormalizedString());
        return Optional.ofNullable(node);
    }
}
