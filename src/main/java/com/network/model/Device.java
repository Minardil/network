package com.network.model;

import inet.ipaddr.MACAddressString;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

@NoArgsConstructor
@Accessors(chain = true)
@Validated
@Data
public class Device {
    @NonNull
    private DeviceType deviceType;
    @NonNull
    private MACAddressString macAddress;
    private MACAddressString uplinkMacAddress;
}
