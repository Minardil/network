package com.network.repository;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.network.model.Device;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

//todo make tests
public class DevicesFileWriterImpl implements DevicesFileWriter {
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Override
    public void writeDevice(File file, Device device) throws IOException {
        DeviceJsonBean dto = DeviceJsonBeanMapper.toJson(device);
        String data = jsonMapper.writeValueAsString(dto);
        FileUtils.writeLines(file, Collections.singleton(data), true);
    }
}
