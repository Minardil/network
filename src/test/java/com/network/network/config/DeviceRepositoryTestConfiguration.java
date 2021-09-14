package com.network.network.config;

import com.network.network.utils.DeviceRepositoryInitialDataSupplier;
import com.network.repository.DeviceRepository;
import com.network.repository.MemoryDeviceRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DeviceRepositoryTestConfiguration {

    @Primary
    @Bean
    DeviceRepository testDeviceRepository(DeviceRepositoryInitialDataSupplier factory) {
        return new MemoryDeviceRepositoryImpl(factory.get());
    }
}
