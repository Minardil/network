package com.network.controller.request;

import com.network.dto.DeviceDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Validated
public class RegisterDeviceRequest {

    private DeviceDTO device;
}
