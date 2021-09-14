package com.network.config;

import com.network.repository.DeviceRepository;
import com.network.repository.MemoryDeviceRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceRepositoryConfiguration {

    @Bean
    DeviceRepository deviceRepository() {
        return new MemoryDeviceRepositoryImpl();
    }
}
