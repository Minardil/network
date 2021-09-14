package com.network.network.api.controller;

import com.network.controller.api.NetworkController;
import com.network.dto.DeviceDTO;
import com.network.model.DeviceType;
import com.network.network.utils.JsonUtils;
import com.network.service.NetworkService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Arrays;
import java.util.TreeSet;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NetworkController.class)
class NetworkControllerListDevicesUnitTests {

	private static final String VALID_MAC = "2C:54:91:88:C9:E3";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private NetworkService networkService;

	@Test
	public void testRegisterDevice() throws Exception {
		var deviceDTO1 = new DeviceDTO()
				.setDeviceType(DeviceType.Access)
				.setMacAddress(VALID_MAC)
				.setUplinkMacAddress(null);

		var deviceDTO2 = new DeviceDTO()
				.setDeviceType(DeviceType.Switch)
				.setMacAddress(VALID_MAC)
				.setUplinkMacAddress(null);

		var devices = new TreeSet<>(Arrays.asList(deviceDTO1, deviceDTO2));
		Mockito.when(networkService.listDevices()).thenReturn(devices);

		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/listDevices"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(result -> {
					var response = JsonUtils.deserializeList(result.getResponse().getContentAsString(), DeviceDTO.class);
					Assertions.assertIterableEquals(devices, response);
				});
	}
}
