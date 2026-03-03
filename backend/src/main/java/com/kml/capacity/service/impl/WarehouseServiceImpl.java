package com.kml.capacity.service.impl;

import com.kml.capacity.service.WarehouseService;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.WarehouseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class WarehouseServiceImpl implements WarehouseService {

  private final WarehouseRepository warehouseRepository;

  public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
    this.warehouseRepository = warehouseRepository;
  }

  @Override
  @Transactional
  public Warehouse createWarehouse(String name, String address) {
    Warehouse warehouse = new Warehouse(name, address);
    return warehouseRepository.save(warehouse);
  }

  @Override
  public Optional<Warehouse> getWarehouseById(Long id) {
    return this.warehouseRepository.findById(id);
  }

  @Override
  public Optional<Warehouse> getWarehouseByName(String name) {
    return this.warehouseRepository.findByName(name);
  }

  @Override
  public List<Warehouse> getAllWarehouses() {
    return this.warehouseRepository.findAll();
  }
}
