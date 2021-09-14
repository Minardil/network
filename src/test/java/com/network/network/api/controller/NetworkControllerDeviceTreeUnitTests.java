
package com.network.network.api.controller;

import com.network.controller.api.NetworkController;
import com.network.service.NetworkService;
import inet.ipaddr.MACAddressString;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NetworkController.class)
class NetworkControllerDeviceTreeUnitTests {
	private static final String VALID_MAC = "2C:54:91:88:C9:E3";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private NetworkService networkService;

	@Test
	public void getTree() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/getDevicesTree"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk());

		Mockito.verify(networkService, Mockito.times(1)).getDevicesTree();
	}

	@Test
	public void getSubTreeInvalid() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/getDevicesSubTree")
						.param("mac", "invalid")
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest());

		Mockito.verify(networkService, Mockito.times(0)).getDevicesSubTree(new MACAddressString(VALID_MAC));
	}

	@Test
	public void getSubTreeValid() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/network/getDevicesSubTree")
						.param("mac", VALID_MAC)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound());

		Mockito.verify(networkService, Mockito.times(1)).getDevicesSubTree(new MACAddressString(VALID_MAC));
	}
}
