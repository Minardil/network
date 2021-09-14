package com.network.repository;

public class DeviceAlreadyExistsException extends RuntimeException {
    public DeviceAlreadyExistsException(String macAddress) {
        super("Device with mac address " + macAddress + " already exists");
    }
}
