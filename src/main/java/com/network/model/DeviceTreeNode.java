package com.network.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

@Data
@Accessors(chain = true)
@Validated
@NoArgsConstructor
public class DeviceTreeNode {
    @NonNull
    private Device device;
    private Collection<DeviceTreeNode> nodes;
}
