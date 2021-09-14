package com.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.SortedSet;

@Data
@Accessors(chain = true)
@Validated
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTreeNodeDTO {
    @NonNull
    private DeviceDTO device;
    private Collection<DeviceTreeNodeDTO> nodes;
}
