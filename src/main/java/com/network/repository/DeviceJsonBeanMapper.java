package com.network.repository;

import com.network.model.Device;
import inet.ipaddr.MACAddressString;

public class DeviceJsonBeanMapper {
        public static DeviceJsonBean toJson(Device device) {
            return new DeviceJsonBean()
                    .withDeviceType(device.getDeviceType())
                    .withMacAddress(device.getMacAddress().toNormalizedString())
                    .withUplinkMacAddress(device.getUplinkMacAddress() != null ? device.getUplinkMacAddress().toNormalizedString() : null);
        }

        public static Device toDevice(DeviceJsonBean deviceJsonBean) {
            return new Device()
                    .setDeviceType(deviceJsonBean.deviceType)
                    .setMacAddress(new MACAddressString(deviceJsonBean.macAddress))
                    .setUplinkMacAddress(new MACAddressString(deviceJsonBean.uplinkMacAddress));
        }
    }
