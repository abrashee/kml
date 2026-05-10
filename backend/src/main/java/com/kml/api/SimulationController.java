package com.kml.api;

import com.kml.capacity.dto.ai.ShipmentSimulationEventDto;
import com.kml.capacity.service.SimulationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulations")
public class SimulationController {

  private final SimulationService simulationService;

  public SimulationController(SimulationService simulationService) {
    this.simulationService = simulationService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping("/start/{shipmentId}")
  public ResponseEntity<Void> startSimulation(@PathVariable @Min(1) Long shipmentId) {
    simulationService.startSimulation(shipmentId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping("/event")
  public ResponseEntity<Void> processSimulationEvent(
      @Valid @RequestBody ShipmentSimulationEventDto event) {
    simulationService.processSimulationEvent(event);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{shipmentId}")
  public ResponseEntity<ShipmentSimulationEventDto> getSimulationState(
      @PathVariable @Min(1) Long shipmentId) {

    ShipmentSimulationEventDto state = simulationService.getSimulationState(shipmentId);

    if (state == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(state);
  }
}
