package com.network.controller.api;

import com.network.controller.request.RegisterDeviceRequest;
import com.network.dto.DeviceDTO;
import com.network.dto.DeviceTreeNodeDTO;
import com.network.repository.DeviceAlreadyExistsException;
import com.network.repository.DeviceIsNotRegisteredException;
import com.network.service.NetworkService;
import inet.ipaddr.MACAddressString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/network")
public class NetworkController {

    private final NetworkService networkService;

    @Autowired
    NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    @PostMapping(value = "/registerDevice", consumes = "application/json")
    public ResponseEntity<DeviceDTO> registerDevice(@RequestBody RegisterDeviceRequest registerDeviceRequest) {
        var deviceDTO = registerDeviceRequest.getDevice();

        var macAddressString = new MACAddressString(deviceDTO.getMacAddress());
        var uplinkMacAddressString = new MACAddressString(deviceDTO.getUplinkMacAddress());

        if (!macAddressString.isValid() || macAddressString.isEmpty() || !uplinkMacAddressString.isValid()) {
            return ResponseEntity.badRequest().build();
        }

        if (Objects.equals(macAddressString.toNormalizedString(), uplinkMacAddressString.toNormalizedString())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            DeviceDTO device = networkService.registerDevice(deviceDTO);
            return ResponseEntity.ok(device);
        } catch (DeviceAlreadyExistsException | DeviceIsNotRegisteredException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/getDeviceByMac")
    public ResponseEntity<DeviceDTO> getDeviceByMac(@RequestParam("mac") String deviceMac) {
        var macAddressString = new MACAddressString(deviceMac);

        if (!macAddressString.isValid() || macAddressString.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<DeviceDTO> deviceDTO = networkService.getDeviceByMacAddress(macAddressString);
        if (deviceDTO.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(deviceDTO.get());
        }
    }

    @GetMapping(value = "/listDevices")
    public ResponseEntity<Collection<DeviceDTO>> listDevices() {
        Collection<DeviceDTO> devices = networkService.listDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping(value = "/getDevicesTree")
    public ResponseEntity<Collection<DeviceTreeNodeDTO>> getDevicesTree() {
        Collection<DeviceTreeNodeDTO> devices = networkService.getDevicesTree();
        return ResponseEntity.ok(devices);
    }

    @GetMapping(value = "/getDevicesSubTree")
    public ResponseEntity<DeviceTreeNodeDTO> getDevicesSubTree(@RequestParam("mac") String startingNodeMac) {
        var macAddressString = new MACAddressString(startingNodeMac);

        if (!macAddressString.isValid() || macAddressString.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<DeviceTreeNodeDTO> devices = networkService.getDevicesSubTree(macAddressString);
        if (devices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(devices.get());
    }
}
