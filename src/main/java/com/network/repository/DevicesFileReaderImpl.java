package com.network.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.network.model.Device;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

//todo make tests
public class DevicesFileReaderImpl implements DevicesFileReader {
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Override
    public Collection<Device> readDevices(File file) throws IOException {
        if (!file.isFile()) {
            return Collections.emptyList();
        }
        LineIterator iterator = FileUtils.lineIterator(file);
        return readDevices(iterator);
    }

    private Collection<Device> readDevices(LineIterator iterator) throws JsonProcessingException {
        Collection<Device> devices = new ArrayList<>();
        while (iterator.hasNext()) {
            Device device = createDeviceFromJsonDTO(iterator);
            devices.add(device);
        }
        return devices;
    }

    private Device createDeviceFromJsonDTO(LineIterator iterator) throws JsonProcessingException {
        DeviceJsonBean deviceJsonBean = jsonMapper.readValue(iterator.next(), DeviceJsonBean.class);
        return DeviceJsonBeanMapper.toDevice(deviceJsonBean);
    }
}
