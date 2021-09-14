package com.network.network.api.controller;

import com.network.controller.api.NetworkController;
import com.network.dto.DeviceDTO;
import com.network.model.DeviceType;
import com.network.network.utils.JsonUtils;
import com.network.service.NetworkService;
import inet.ipaddr.MACAddressString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NetworkController.class)
class NetworkControllerGetDeviceByIdUnitTests {

	private static final String VALID_MAC = "2C:54:91:88:C9:E3";
	private static final String VALID_MAC_2 = "2C:54:91:88:C9:E4";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private NetworkService networkService;

	@Test
	public void testInvalidMac() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/getDeviceByMac")
						.param("mac", "invalid")
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testEmptyMac() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/getDeviceByMac")
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetDevice() throws Exception {
		var mac = new MACAddressString(VALID_MAC);
		var deviceDTO = new DeviceDTO().setMacAddress(VALID_MAC).setDeviceType(DeviceType.Gateway).setUplinkMacAddress(VALID_MAC_2);

		Mockito.when(networkService.getDeviceByMacAddress(mac)).thenReturn(Optional.of(deviceDTO));
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/getDeviceByMac")
						.param("mac", VALID_MAC)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(result -> {
					var response = JsonUtils.deserialize(result.getResponse().getContentAsString(), DeviceDTO.class);
					Assertions.assertEquals(deviceDTO, response);
				});
	}

	@Test
	public void testNotFound() throws Exception {
		var mac = new MACAddressString(VALID_MAC_2);

		Mockito.when(networkService.getDeviceByMacAddress(mac)).thenReturn(Optional.empty());
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/getDeviceByMac")
						.param("mac", VALID_MAC_2)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound());
	}
}
