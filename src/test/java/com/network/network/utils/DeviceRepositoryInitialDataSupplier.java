package com.network.network.utils;

import com.network.model.Device;
import com.network.model.DeviceType;
import inet.ipaddr.MACAddressString;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@Component
public class DeviceRepositoryInitialDataSupplier implements Supplier<Collection<Device>> {

    private final List<Device> devices;

    public DeviceRepositoryInitialDataSupplier() {
        devices = Arrays.asList(
                new Device()
                        .setDeviceType(DeviceType.Switch)
                        .setMacAddress(new MACAddressString("00:25:85:FF:FE:12:34:56")),
                new Device()
                        .setDeviceType(DeviceType.Switch)
                        .setMacAddress(new MACAddressString("00:25:81:FF:FE:12:34:56"))
                        .setUplinkMacAddress(new MACAddressString("00:25:85:FF:FE:12:34:56")),
                new Device()
                        .setDeviceType(DeviceType.Switch)
                        .setMacAddress(new MACAddressString("00:25:82:FF:FE:12:34:56"))
                        .setUplinkMacAddress(new MACAddressString("00:25:81:FF:FE:12:34:56")),
                new Device()
                        .setDeviceType(DeviceType.Switch)
                        .setMacAddress(new MACAddressString("00:25:83:FF:FE:12:34:56"))
                        .setUplinkMacAddress(new MACAddressString("00:25:85:FF:FE:12:34:56")),
                new Device()
                        .setDeviceType(DeviceType.Switch)
                        .setMacAddress(new MACAddressString("00:25:84:FF:FE:12:34:56"))
        );;
    }

    public Collection<Device> get() {
        return devices;
    }
}
