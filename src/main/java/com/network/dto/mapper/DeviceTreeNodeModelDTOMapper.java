package com.network.dto.mapper;

import com.network.dto.DeviceTreeNodeDTO;
import com.network.model.DeviceTreeNode;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class DeviceTreeNodeModelDTOMapper {

    //todo extract field filtering to controller level
    public static DeviceTreeNodeDTO mapToDTO(DeviceTreeNode node, Set<DeviceModelDTOMapper.Fields> fields) {
        return new DeviceTreeNodeDTO()
                .setDevice(DeviceModelDTOMapper.mapToDTO(node.getDevice(), fields))
                .setNodes(mapToDTOs(node.getNodes(), fields));
    }

    //todo extract field filtering to controller level
    public static Collection<DeviceTreeNodeDTO> mapToDTOs(Collection<DeviceTreeNode> nodes, Set<DeviceModelDTOMapper.Fields> fields) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        return nodes.stream()
                .map(item -> mapToDTO(item, fields))
                .collect(Collectors.toUnmodifiableList());
    }
}
