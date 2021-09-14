package com.network.repository;

import com.network.model.Device;

import java.io.File;
import java.io.IOException;

public interface DevicesFileWriter {
    void writeDevice(File file, Device device) throws IOException;
}
