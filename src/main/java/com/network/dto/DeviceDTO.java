package com.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.network.model.DeviceType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Data
@Accessors(chain = true)
@Validated
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDTO {
    private DeviceType deviceType;
    @NonNull
    private String macAddress;
    private String uplinkMacAddress;
}
