package com.network.repository;

import com.network.model.DeviceType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@NoArgsConstructor
@With
@AllArgsConstructor
public class DeviceJsonBean {
    public String macAddress;
    public DeviceType deviceType;
    public String uplinkMacAddress;
}