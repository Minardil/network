package com.network.model;

import java.util.Comparator;

public class DeviceTypeOrder {
    public static final Comparator<DeviceType> COMPARATOR = Comparator.comparingInt(DeviceTypeOrder::getDeviceTypeOrder);

    private static int getDeviceTypeOrder(DeviceType deviceType) {
        switch (deviceType) {
            case Gateway: return 0;
            case Switch: return 1;
            case Access: return 2;
            default: throw new IllegalArgumentException("Unsupported type: " + deviceType);
        }
    }
}
