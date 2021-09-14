package com.network.config;

import com.network.repository.DeviceRepository;
import com.network.repository.DevicesFileReaderImpl;
import com.network.repository.DevicesFileWriterImpl;
import com.network.repository.FileDeviceRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceRepositoryConfiguration {

    @Bean
    DeviceRepository deviceRepository(DeviceRepositoryProperties deviceRepositoryProperties) {
        return new FileDeviceRepositoryImpl(deviceRepositoryProperties, new DevicesFileReaderImpl(), new DevicesFileWriterImpl());
    }
}
