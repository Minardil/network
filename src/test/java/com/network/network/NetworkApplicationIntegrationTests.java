package com.network.network;

import com.network.controller.api.NetworkController;
import com.network.dto.DeviceDTO;
import com.network.dto.DeviceTreeNodeDTO;
import com.network.dto.mapper.DeviceModelDTOMapper;
import com.network.network.utils.DeviceRepositoryInitialDataSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.stream.Collectors;

@SpringBootTest
class NetworkApplicationIntegrationTests {

	@Autowired
	private NetworkController networkController;

	@Autowired
	private DeviceRepositoryInitialDataSupplier initialDataSupplier;

	@Test
	void listDevicesOrdered() {
		Collection<DeviceDTO> response = networkController.listDevices().getBody();
		Assertions.assertNotNull(response);
		Collection<DeviceDTO> deviceDTOS = DeviceModelDTOMapper.mapToDTOs(initialDataSupplier.get(), EnumSet.of(DeviceModelDTOMapper.Fields.DeviceType, DeviceModelDTOMapper.Fields.MacAddress));

		deviceDTOS = deviceDTOS.stream().sorted(Comparator.
				<DeviceDTO>comparingInt(device -> device.getDeviceType().getOrder())
				.thenComparing(DeviceDTO::getMacAddress)
		).collect(Collectors.toUnmodifiableList());

		Assertions.assertEquals(deviceDTOS, response);
	}

	@Test
	void listTree() {
		Collection<DeviceTreeNodeDTO> response = networkController.getDevicesTree().getBody();
		Assertions.assertNotNull(response);
		Assertions.assertEquals(2, response.size());
		//todo rework so test compares exact content instead of size
	}

	@Test
	void listSubTreeNotFound() {
		ResponseEntity<DeviceTreeNodeDTO> response = networkController.getDevicesSubTree("00:25:70:FF:FE:12:34:56");
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
	}

	@Test
	void listSubTreeWithChildrenFound() {
		DeviceTreeNodeDTO response = networkController.getDevicesSubTree("00:25:85:FF:FE:12:34:56").getBody();
		Assertions.assertNotNull(response);
		Assertions.assertEquals(response.getNodes().size(), 2);
	}

	@Test
	void listSubTreeWithoutChildrenFound() {
		DeviceTreeNodeDTO response = networkController.getDevicesSubTree("00:25:84:FF:FE:12:34:56").getBody();
		Assertions.assertNotNull(response);
		Assertions.assertEquals(response.getNodes().size(), 0);
	}
}
