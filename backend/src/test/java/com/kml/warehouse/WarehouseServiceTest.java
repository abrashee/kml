package com.kml.warehouse;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.kml.security.CurrentUserProvider;
import com.kml.user.entity.User;
import com.kml.user.entity.UserRole;
import com.kml.warehouse.dto.WarehouseResponseDto;
import com.kml.warehouse.entity.Warehouse;
import com.kml.warehouse.repository.WarehouseRepository;
import com.kml.warehouse.service.WarehouseServiceImpl;

public class WarehouseServiceTest {

  @Mock private WarehouseRepository warehouseRepository;
  @Mock private CurrentUserProvider currentUserProvider;
  @InjectMocks private WarehouseServiceImpl warehouseService;

  private User owner;

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);

    owner = new User("Owner", "owner", "password", UserRole.USER);

    // Set the ID so ownership checks pass
    Field idField = User.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(owner, 1L);
  }

  @Test
  void testCreateWarehouseSuccessfully() {
    Warehouse warehouse = Warehouse.create(owner, "Main Warehouse", "123 Main St");
    when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

    WarehouseResponseDto saved =
        warehouseService.createWarehouse(owner, "Main Warehouse", "123 Main St");

    assertNotNull(saved);
    assertEquals("Main Warehouse", saved.name());
    assertEquals("123 Main St", saved.address());
    verify(warehouseRepository, times(1)).save(any(Warehouse.class));
  }

  @Test
  void testGetWarehouseById() {
    Warehouse warehouse = Warehouse.create(owner, "Main Warehouse", "123 Main St");
    when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
    when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    Optional<WarehouseResponseDto> found = warehouseService.getWarehouseById(1L);

    assertTrue(found.isPresent());
    assertEquals("Main Warehouse", found.get().name());
    assertEquals("123 Main St", found.get().address());
  }

  @Test
  void testGetAllWarehouses() {
    Warehouse warehouse = Warehouse.create(owner, "Main Warehouse", "123 Main St");
    when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));
    when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    List<WarehouseResponseDto> list = warehouseService.getAllWarehouses();

    assertEquals(1, list.size());
    assertEquals("Main Warehouse", list.get(0).name());
    assertEquals("123 Main St", list.get(0).address());
  }
}
