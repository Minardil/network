package com.network.repository;

import com.network.model.Device;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface DevicesFileReader {
    Collection<Device> readDevices(File file) throws IOException;
}
