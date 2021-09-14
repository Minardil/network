package com.network.network.config;

import com.network.config.DeviceRepositoryProperties;
import com.network.network.utils.DeviceRepositoryInitialDataSupplier;
import com.network.repository.DeviceRepository;
import com.network.repository.FileDeviceRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DeviceRepositoryTestConfiguration {

    @Primary
    @Bean
    DeviceRepository testDeviceRepository(DeviceRepositoryInitialDataSupplier factory, DeviceRepositoryProperties deviceRepositoryProperties) {
        return new FileDeviceRepositoryImpl(deviceRepositoryProperties, file -> factory.get(), (file, device) -> {});
    }
}
