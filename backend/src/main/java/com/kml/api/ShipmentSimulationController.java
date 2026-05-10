package com.kml.api;

import com.kml.capacity.dto.ai.ShipmentSimulationEventDto;
import com.kml.capacity.service.SimulationService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentSimulationController {

  private final SimulationService simulationService;

  public ShipmentSimulationController(SimulationService simulationService) {
    this.simulationService = simulationService;
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{shipmentId}/simulation")
  public ResponseEntity<ShipmentSimulationEventDto> getSimulationState(
      @PathVariable @Min(1) Long shipmentId) {

    ShipmentSimulationEventDto state = simulationService.getSimulationState(shipmentId);

    if (state == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(state);
  }
}
