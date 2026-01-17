package com.kml.infra;

import com.kml.domain.warehouse.StorageUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long> {}
