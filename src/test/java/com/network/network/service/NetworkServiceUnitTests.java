package com.network.network.service;

import com.network.dto.DeviceDTO;
import com.network.dto.DeviceTreeNodeDTO;
import com.network.model.DeviceType;
import com.network.repository.DeviceAlreadyExistsException;
import com.network.repository.DeviceIsNotRegisteredException;
import com.network.repository.DeviceRepository;
import com.network.service.NetworkService;
import com.network.service.NetworkServiceImpl;
import inet.ipaddr.MACAddressString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@SpringBootTest
public class NetworkServiceUnitTests {
    private static final String VALID_MAC = "2c:54:91:88:c9:e3";
    private static final String VALID_MAC_2 = "2c:54:92:88:c9:e3";
    private static final String VALID_MAC_3 = "2c:54:93:88:c9:e3";

    @MockBean
    private DeviceRepository deviceRepository;

    private NetworkService networkService;

    @BeforeEach
    public void beforeAll() {
        networkService = new NetworkServiceImpl(deviceRepository);
    }

    @Test
    public void testRegisterDeviceTwice() {
        Assertions.assertThrows(DeviceAlreadyExistsException.class, () -> {
            DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
            networkService.registerDevice(deviceDTO);
            networkService.registerDevice(deviceDTO);
        });
    }

    @Test
    public void testRegisterNonExistentParent() {
        Assertions.assertThrows(DeviceIsNotRegisteredException.class, () -> {
            DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setUplinkMacAddress(VALID_MAC);
            networkService.registerDevice(deviceDTO);
        });
    }

    @Test
    public void saveAndGet() {
        DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
        networkService.registerDevice(deviceDTO);

        Optional<DeviceDTO> readDevice = networkService.getDeviceByMacAddress(new MACAddressString(deviceDTO.getMacAddress()));
        Assertions.assertTrue(readDevice.isPresent());
        Assertions.assertEquals(deviceDTO, readDevice.get());
    }

    @Test
    public void saveAndGetTree() {
        DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
        DeviceDTO deviceDTOChild = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_2).setUplinkMacAddress(VALID_MAC);
        DeviceDTO deviceDTO2 = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_3);

        networkService.registerDevice(deviceDTO);
        networkService.registerDevice(deviceDTOChild);
        networkService.registerDevice(deviceDTO2);

        Collection<DeviceTreeNodeDTO> readDevice = networkService.getDevicesTree();
        Assertions.assertEquals(2, readDevice.size());

        Iterator<DeviceTreeNodeDTO> parentIterator = readDevice.iterator();

        DeviceTreeNodeDTO parentNode = parentIterator.next();
        Assertions.assertEquals(VALID_MAC, parentNode.getDevice().getMacAddress());

        DeviceTreeNodeDTO parentNode2 = parentIterator.next();
        Assertions.assertEquals(VALID_MAC_3, parentNode2.getDevice().getMacAddress());

        Assertions.assertEquals(1, parentNode.getNodes().size());

        DeviceTreeNodeDTO childNode = parentNode.getNodes().iterator().next();
        Assertions.assertEquals(VALID_MAC_2, childNode.getDevice().getMacAddress());
    }

    @Test
    public void saveAndGetSubtree() {
        DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
        DeviceDTO deviceDTOChild = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_2).setUplinkMacAddress(VALID_MAC);
        DeviceDTO deviceDTO2 = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_3);

        networkService.registerDevice(deviceDTO);
        networkService.registerDevice(deviceDTOChild);
        networkService.registerDevice(deviceDTO2);

        Optional<DeviceTreeNodeDTO> readDevice = networkService.getDevicesSubTree(new MACAddressString(VALID_MAC));
        Assertions.assertTrue(readDevice.isPresent());

        Assertions.assertEquals(1, readDevice.get().getNodes().size());

        DeviceTreeNodeDTO childNode = readDevice.get().getNodes().iterator().next();
        Assertions.assertEquals(VALID_MAC_2, childNode.getDevice().getMacAddress());
    }
}
