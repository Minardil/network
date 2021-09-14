package com.network.network.service.device;

import com.network.dto.DeviceDTO;
import com.network.dto.DeviceTreeNodeDTO;
import com.network.model.DeviceType;
import com.network.repository.DeviceAlreadyExistsException;
import com.network.repository.DeviceIsNotRegisteredException;
import com.network.repository.DeviceRepository;
import com.network.service.device.DevicesService;
import com.network.service.device.DevicesServiceImpl;
import com.network.service.device.cache.DeviceNodesCacheImpl;
import inet.ipaddr.MACAddressString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@SpringBootTest
public class DevicesServiceUnitTests {
    private static final String VALID_MAC = "2c:54:91:88:c9:e3";
    private static final String VALID_MAC_2 = "2c:54:92:88:c9:e3";
    private static final String VALID_MAC_3 = "2c:54:93:88:c9:e3";

    private DeviceRepository deviceRepository;
    private DevicesService devicesService;
    private DeviceNodesCacheImpl deviceNodesCache;

    @BeforeEach
    public void beforeAll() {
        deviceRepository = Mockito.mock(DeviceRepository.class);
        deviceNodesCache = Mockito.spy(new DeviceNodesCacheImpl());
        devicesService = new DevicesServiceImpl(deviceRepository, deviceNodesCache);
    }

    @Test
    public void testRegisterDeviceTwice() {
        Assertions.assertThrows(DeviceAlreadyExistsException.class, () -> {
            DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
            devicesService.registerDevice(deviceDTO);
            devicesService.registerDevice(deviceDTO);

            checkRepositorySaveInvoked(1);
            checkRepositoryFetchIsNotInvoked();
        });
    }

    @Test
    public void testRegisterNonExistentParent() {
        Assertions.assertThrows(DeviceIsNotRegisteredException.class, () -> {
            DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setUplinkMacAddress(VALID_MAC);
            devicesService.registerDevice(deviceDTO);

            checkRepositorySaveInvoked(0);
            checkRepositoryFetchIsNotInvoked();
        });
    }

    @Test
    public void saveAndGet() {
        DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
        devicesService.registerDevice(deviceDTO);

        Mockito.verify(deviceNodesCache, Mockito.times(1)).addNode(Mockito.any());
        Mockito.verify(deviceRepository, Mockito.times(1)).addDevice(Mockito.any());
        Mockito.verify(deviceNodesCache, Mockito.times(1)).getNode(Mockito.any());
        Mockito.verify(deviceNodesCache, Mockito.times(0)).attachNode(Mockito.any(), Mockito.any());


        Optional<DeviceDTO> readDevice = devicesService.getDeviceByMacAddress(new MACAddressString(deviceDTO.getMacAddress()));
        Assertions.assertTrue(readDevice.isPresent());
        Assertions.assertEquals(deviceDTO, readDevice.get());

        checkRepositorySaveInvoked(1);
        checkRepositoryFetchIsNotInvoked();
    }

    @Test
    public void saveAndGetTree() {
        DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
        DeviceDTO deviceDTOChild = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_2).setUplinkMacAddress(VALID_MAC);
        DeviceDTO deviceDTO2 = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_3);

        devicesService.registerDevice(deviceDTO);
        devicesService.registerDevice(deviceDTOChild);
        devicesService.registerDevice(deviceDTO2);

        Mockito.verify(deviceNodesCache, Mockito.times(1)).attachNode(Mockito.any(), Mockito.any());

        Collection<DeviceTreeNodeDTO> readDevice = devicesService.getDevicesTree();
        Assertions.assertEquals(2, readDevice.size());

        Iterator<DeviceTreeNodeDTO> parentIterator = readDevice.iterator();

        DeviceTreeNodeDTO parentNode = parentIterator.next();
        Assertions.assertEquals(VALID_MAC, parentNode.getDevice().getMacAddress());

        DeviceTreeNodeDTO parentNode2 = parentIterator.next();
        Assertions.assertEquals(VALID_MAC_3, parentNode2.getDevice().getMacAddress());

        Assertions.assertEquals(1, parentNode.getNodes().size());

        DeviceTreeNodeDTO childNode = parentNode.getNodes().iterator().next();
        Assertions.assertEquals(VALID_MAC_2, childNode.getDevice().getMacAddress());

        checkRepositorySaveInvoked(3);
        checkRepositoryFetchIsNotInvoked();
    }

    @Test
    public void saveAndGetSubtree() {
        DeviceDTO deviceDTO = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC);
        DeviceDTO deviceDTOChild = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_2).setUplinkMacAddress(VALID_MAC);
        DeviceDTO deviceDTO2 = new DeviceDTO().setDeviceType(DeviceType.Switch).setMacAddress(VALID_MAC_3);

        devicesService.registerDevice(deviceDTO);
        devicesService.registerDevice(deviceDTOChild);
        devicesService.registerDevice(deviceDTO2);

        Optional<DeviceTreeNodeDTO> readDevice = devicesService.getNode(new MACAddressString(VALID_MAC));
        Assertions.assertTrue(readDevice.isPresent());

        Assertions.assertEquals(1, readDevice.get().getNodes().size());

        DeviceTreeNodeDTO childNode = readDevice.get().getNodes().iterator().next();
        Assertions.assertEquals(VALID_MAC_2, childNode.getDevice().getMacAddress());

        checkRepositorySaveInvoked(3);
        checkRepositoryFetchIsNotInvoked();
    }

    private void checkRepositoryFetchIsNotInvoked() {
        //1 because is called during test initialization
        Mockito.verify(deviceRepository, Mockito.times(1)).getAllDevices();
    }

    private void checkRepositorySaveInvoked(int times) {
        Mockito.verify(deviceRepository, Mockito.times(times)).addDevice(Mockito.any());
    }
}
