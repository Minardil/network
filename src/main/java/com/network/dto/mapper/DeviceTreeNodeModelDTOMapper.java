package com.network.dto.mapper;

import com.network.dto.DeviceTreeNodeDTO;
import com.network.model.DeviceTreeNode;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class DeviceTreeNodeModelDTOMapper {

    public static DeviceTreeNodeDTO mapToDTO(DeviceTreeNode node) {
        return new DeviceTreeNodeDTO()
                .setDevice(DeviceModelDTOMapper.mapToDTO(node.getDevice()))
                .setNodes(mapToDTOs(node.getNodes()));
    }

    public static Collection<DeviceTreeNodeDTO> mapToDTOs(Collection<DeviceTreeNode> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        return nodes.stream()
                .map(DeviceTreeNodeModelDTOMapper::mapToDTO)
                .collect(Collectors.toUnmodifiableList());
    }
}
