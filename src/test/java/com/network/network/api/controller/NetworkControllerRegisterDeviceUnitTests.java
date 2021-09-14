package com.network.network.api.controller;

import com.network.controller.api.NetworkController;
import com.network.controller.request.RegisterDeviceRequest;
import com.network.dto.DeviceDTO;
import com.network.model.DeviceType;
import com.network.network.utils.JsonUtils;
import com.network.service.device.DevicesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NetworkController.class)
class NetworkControllerRegisterDeviceUnitTests {

	private static final String VALID_MAC = "2C:54:91:88:C9:E3";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DevicesService devicesService;

	@Test
	public void testRegisterDevice() throws Exception {
		var deviceDTO = new DeviceDTO()
				.setDeviceType(DeviceType.Access)
				.setMacAddress(VALID_MAC)
				.setUplinkMacAddress(null);

		Mockito.when(devicesService.registerDevice(deviceDTO)).thenReturn(deviceDTO);

		var registerDeviceRequest = new RegisterDeviceRequest().setDevice(deviceDTO);
		var requestBody = JsonUtils.serialize(registerDeviceRequest);

		this.mockMvc.perform(post("/api/v1/network/registerDevice")
						.contentType("application/json")
						.content(requestBody)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(result -> {
					var response = JsonUtils.deserialize(result.getResponse().getContentAsString(), DeviceDTO.class);
					Assertions.assertEquals(deviceDTO, response);
				});
	}

	@Test
	public void testInvalidMacAddress() throws Exception {
		var registerDeviceRequest = new RegisterDeviceRequest().setDevice(
				new DeviceDTO()
						.setDeviceType(DeviceType.Access)
						.setMacAddress("invalid")
						.setUplinkMacAddress(null)
		);
		var requestBody = JsonUtils.serialize(registerDeviceRequest);

		this.mockMvc.perform(post("/api/v1/network/registerDevice")
						.contentType("application/json")
						.content(requestBody)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testEmptyMacAddress() throws Exception {
		var registerDeviceRequest = new RegisterDeviceRequest().setDevice(
				new DeviceDTO()
						.setDeviceType(DeviceType.Access)
						.setMacAddress("")
						.setUplinkMacAddress(null)
		);
		var requestBody = JsonUtils.serialize(registerDeviceRequest);

		this.mockMvc.perform(post("/api/v1/network/registerDevice")
						.contentType("application/json")
						.content(requestBody)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testInvalidUplinkMacAddress() throws Exception {
		var registerDeviceRequest = new RegisterDeviceRequest().setDevice(
				new DeviceDTO()
						.setDeviceType(DeviceType.Access)
						.setMacAddress(VALID_MAC)
						.setUplinkMacAddress("invalid")
		);
		var requestBody = JsonUtils.serialize(registerDeviceRequest);

		this.mockMvc.perform(post("/api/v1/network/registerDevice")
						.contentType("application/json")
						.content(requestBody)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testSameMacs() throws Exception {
		var registerDeviceRequest = new RegisterDeviceRequest().setDevice(
				new DeviceDTO()
						.setDeviceType(DeviceType.Access)
						.setMacAddress(VALID_MAC)
						.setUplinkMacAddress(VALID_MAC)
		);
		var requestBody = JsonUtils.serialize(registerDeviceRequest);

		this.mockMvc.perform(post("/api/v1/network/registerDevice")
						.contentType("application/json")
						.content(requestBody)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest());
	}
}
