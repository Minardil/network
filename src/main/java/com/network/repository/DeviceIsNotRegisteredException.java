package com.network.repository;

public class DeviceIsNotRegisteredException extends RuntimeException {
    public DeviceIsNotRegisteredException(String macAddress) {
        super("Device with mac address " + macAddress + " is not registered");
    }
}
