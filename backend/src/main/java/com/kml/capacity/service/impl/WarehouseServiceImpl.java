package com.kml.capacity.service.impl;

import com.kml.capacity.service.WarehouseService;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.WarehouseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional(readOnly = true)
  public Optional<Warehouse> getWarehouseById(Long id) {
    return this.warehouseRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Warehouse> getWarehouseByName(String name) {
    return this.warehouseRepository.findByName(name);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Warehouse> getAllWarehouses() {
    return this.warehouseRepository.findAll();
  }
}
