package com.kml.warehouse;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.kml.inventory.entity.InventoryItem;
import com.kml.inventory.repository.InventoryRepository;
import com.kml.security.CurrentUserProvider;
import com.kml.user.entity.User;
import com.kml.user.entity.UserRole;
import com.kml.warehouse.storageUnit.dto.StorageUnitInventoryAssignmentDto;
import com.kml.warehouse.storageUnit.entity.StorageUnit;
import com.kml.warehouse.storageUnit.entity.StorageUnitInventoryAssignment;
import com.kml.warehouse.storageUnit.repository.StorageUnitInventoryAssignmentRepository;
import com.kml.warehouse.storageUnit.repository.StorageUnitRepository;
import com.kml.warehouse.storageUnit.service.StorageUnitInventoryAssignmentServiceImpl;


public class StorageUnitInventoryAssignmentServiceTest {

  @Mock private StorageUnitInventoryAssignmentRepository assignmentRepository;

  @Mock private StorageUnitRepository storageUnitRepository;

  @Mock private InventoryRepository inventoryRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private StorageUnitInventoryAssignmentServiceImpl assignmentService;

  private User user;
  private StorageUnit storageUnit;
  private InventoryItem inventoryItem;

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);

    user = new User("TestUser", "testuser", "password", UserRole.USER);
    setId(user, 1L);

    storageUnit = StorageUnit.create(user, "SU-001", 50);
    setId(storageUnit, 1L);

    inventoryItem = InventoryItem.create("SKU-001", "Item1", 100, user);
    setId(inventoryItem, 1L);

    when(currentUserProvider.getCurrentUser()).thenReturn(user);
  }

  @Test
  void testCreateAssignmentSuccessfully() throws Exception {
    // Mocks
    when(storageUnitRepository.findById(storageUnit.getId())).thenReturn(Optional.of(storageUnit));
    when(inventoryRepository.findById(inventoryItem.getId()))
        .thenReturn(Optional.of(inventoryItem));
    when(assignmentRepository.findByStorageUnit_IdAndInventoryItem_Id(
            storageUnit.getId(), inventoryItem.getId()))
        .thenReturn(Optional.empty());

    StorageUnitInventoryAssignment savedAssignment =
        StorageUnitInventoryAssignment.create(user, storageUnit, inventoryItem, 5);
    setId(savedAssignment, 1L);
    when(assignmentRepository.save(any(StorageUnitInventoryAssignment.class)))
        .thenReturn(savedAssignment);

    // Call service
    StorageUnitInventoryAssignmentDto dto =
        assignmentService.createStorageUnitInventoryAssignment(
            storageUnit.getId(), inventoryItem.getId(), 5);

    assertNotNull(dto);
    assertEquals(5, dto.assignedQuantity());
    verify(assignmentRepository, times(1)).save(any(StorageUnitInventoryAssignment.class));
  }

  @Test
  void testCreateAssignmentFailsWhenQuantityExceedsInventory() {
    // Mocks
    when(storageUnitRepository.findById(storageUnit.getId())).thenReturn(Optional.of(storageUnit));
    when(inventoryRepository.findById(inventoryItem.getId()))
        .thenReturn(Optional.of(inventoryItem));
    when(assignmentRepository.findByStorageUnit_IdAndInventoryItem_Id(
            storageUnit.getId(), inventoryItem.getId()))
        .thenReturn(Optional.empty());

    // Assign more than inventory quantity
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              assignmentService.createStorageUnitInventoryAssignment(
                  storageUnit.getId(), inventoryItem.getId(), 1000);
            });

    assertEquals("Quantity exceeds storage unit capacity", exception.getMessage());
  }

  // Helper to set private IDs using reflection
  private void setId(Object entity, Long id) throws Exception {
    Field field = entity.getClass().getDeclaredField("id");
    field.setAccessible(true);
    field.set(entity, id);
  }
}
