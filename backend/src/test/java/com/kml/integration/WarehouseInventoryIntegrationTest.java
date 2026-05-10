package com.kml.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kml.capacity.dto.storageUnit.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.dto.storageUnit.StorageUnitResponseDto;
import com.kml.capacity.dto.warehouse.WarehouseResponseDto;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.StorageUnitInventoryAssignmentService;
import com.kml.capacity.service.StorageUnitService;
import com.kml.capacity.service.WarehouseService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.InventoryRepository;
import com.kml.infra.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WarehouseInventoryIntegrationTest {

  @Autowired private WarehouseService warehouseService;
  @Autowired private StorageUnitService storageUnitService;
  @Autowired private StorageUnitInventoryAssignmentService assignmentService;
  @Autowired private UserRepository userRepository;
  @Autowired private InventoryRepository inventoryRepository;
  @MockBean private CurrentUserProvider currentUserProvider;

  private User owner;
  private User anotherUser;

  @BeforeEach
  void setup() {
    owner = new User("Warehouse Owner", "warehouseowner", "password123", UserRole.USER);
    anotherUser = new User("Another User", "anotherwarehouseuser", "password456", UserRole.USER);

    owner = userRepository.save(owner);
    anotherUser = userRepository.save(anotherUser);
  }

  @Test
  void testCreateWarehouseWithStorageUnitsAndInventory() {
    // Create warehouse
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Main Warehouse", "123 Main St");

    // Create storage units
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto unit1 =
        storageUnitService.createStorageUnit("UNIT-001", warehouse.id(), 100);
    StorageUnitResponseDto unit2 =
        storageUnitService.createStorageUnit("UNIT-002", warehouse.id(), 50);

    assertNotNull(unit1);
    assertNotNull(unit2);
    assertEquals("UNIT-001", unit1.code());
    assertEquals("UNIT-002", unit2.code());
  }

  @Test
  void testAssignInventoryToStorageUnit() {
    // Setup warehouse and storage unit
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse A", "Address A");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-A1", warehouse.id(), 100);

    // Create inventory item
    InventoryItem inventoryItem = new InventoryItem(owner, "SKU-001", "Product Name", 50);
    inventoryItem = inventoryRepository.save(inventoryItem);

    // Assign inventory to storage unit
    StorageUnitInventoryAssignmentDto assignment =
        assignmentService.createStorageUnitInventoryAssignment(
            storageUnit.id(), inventoryItem.getId(), 30);

    assertNotNull(assignment);
    assertEquals(storageUnit.id(), assignment.storageUnitId());
    assertEquals(inventoryItem.getId(), assignment.inventoryItemId());
    assertEquals(30, assignment.assignedQuantity());
  }

  @Test
  void testAssignmentCannotExceedStorageCapacity() {
    // Create warehouse with small storage unit
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Small Warehouse", "Small Address");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto smallUnit =
        storageUnitService.createStorageUnit("SMALL-001", warehouse.id(), 10);

    // Create inventory item
    InventoryItem tempItem = new InventoryItem(owner, "SKU-002", "Large Product", 100);
    final InventoryItem inventoryItem = inventoryRepository.save(tempItem);

    // Try to assign more than capacity
    assertThrows(
        IllegalArgumentException.class,
        () ->
            assignmentService.createStorageUnitInventoryAssignment(
                smallUnit.id(), inventoryItem.getId(), 20));
  }

  @Test
  void testAssignmentWithinCapacity() {
    // Create warehouse with storage unit
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse B", "Address B");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-B1", warehouse.id(), 100);

    // Create inventory items
    InventoryItem item1 = new InventoryItem(owner, "SKU-003", "Item 1", 50);
    InventoryItem item2 = new InventoryItem(owner, "SKU-004", "Item 2", 60);
    item1 = inventoryRepository.save(item1);
    item2 = inventoryRepository.save(item2);

    // Assign both items to same unit without exceeding capacity
    StorageUnitInventoryAssignmentDto assign1 =
        assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item1.getId(), 40);
    StorageUnitInventoryAssignmentDto assign2 =
        assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item2.getId(), 60);

    assertNotNull(assign1);
    assertNotNull(assign2);
    assertEquals(40, assign1.assignedQuantity());
    assertEquals(60, assign2.assignedQuantity());
  }

  @Test
  void testDuplicateAssignmentThrowsError() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse C", "Address C");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-C1", warehouse.id(), 100);

    InventoryItem tempItem = new InventoryItem(owner, "SKU-005", "Test Item", 50);
    final InventoryItem item = inventoryRepository.save(tempItem);

    // Create assignment
    assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item.getId(), 25);

    // Try to create duplicate
    assertThrows(
        IllegalArgumentException.class,
        () ->
            assignmentService.createStorageUnitInventoryAssignment(
                storageUnit.id(), item.getId(), 30));
  }

  @Test
  void testUpdateAssignmentQuantity() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse D", "Address D");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-D1", warehouse.id(), 100);

    InventoryItem item = new InventoryItem(owner, "SKU-006", "Item to Update", 75);
    item = inventoryRepository.save(item);

    StorageUnitInventoryAssignmentDto assignment =
        assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item.getId(), 50);

    // Update quantity
    StorageUnitInventoryAssignmentDto updated =
        assignmentService.updateStorageUnitInventoryAssignment(assignment.id(), 70);

    assertEquals(70, updated.assignedQuantity());
  }

  @Test
  void testUpdateAssignmentQuantityCannotExceedCapacity() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse E", "Address E");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-E1", warehouse.id(), 50);

    InventoryItem item = new InventoryItem(owner, "SKU-007", "Item for Update Test", 100);
    item = inventoryRepository.save(item);

    StorageUnitInventoryAssignmentDto assignment =
        assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item.getId(), 40);

    // Try to update to exceed capacity
    assertThrows(
        IllegalArgumentException.class,
        () -> assignmentService.updateStorageUnitInventoryAssignment(assignment.id(), 60));
  }

  @Test
  void testGetAssignmentsByStorageUnit() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse F", "Address F");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-F1", warehouse.id(), 200);

    InventoryItem item1 = new InventoryItem(owner, "SKU-008", "Item 1", 50);
    InventoryItem item2 = new InventoryItem(owner, "SKU-009", "Item 2", 60);
    item1 = inventoryRepository.save(item1);
    item2 = inventoryRepository.save(item2);

    assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item1.getId(), 40);
    assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item2.getId(), 50);

    List<StorageUnitInventoryAssignmentDto> assignments =
        assignmentService.getByStorageUnitId(storageUnit.id());

    assertEquals(2, assignments.size());
  }

  @Test
  void testGetAssignmentByStorageUnitAndInventoryItem() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse G", "Address G");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-G1", warehouse.id(), 100);

    InventoryItem item = new InventoryItem(owner, "SKU-010", "Specific Item", 75);
    item = inventoryRepository.save(item);

    assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item.getId(), 35);

    StorageUnitInventoryAssignmentDto retrieved =
        assignmentService.getByStorageUnitIdAndInventoryItemId(storageUnit.id(), item.getId());

    assertNotNull(retrieved);
    assertEquals(35, retrieved.assignedQuantity());
  }

  @Test
  void testDeleteAssignment() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Warehouse H", "Address H");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-H1", warehouse.id(), 100);

    InventoryItem tempItem = new InventoryItem(owner, "SKU-011", "Item to Delete", 50);
    final InventoryItem item = inventoryRepository.save(tempItem);

    StorageUnitInventoryAssignmentDto assignment =
        assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item.getId(), 25);

    // Delete assignment
    assignmentService.deleteStorageUnitInventoryItemAssignment(assignment.id());

    // Verify it cannot be retrieved
    assertThrows(
        IllegalArgumentException.class,
        () ->
            assignmentService.getByStorageUnitIdAndInventoryItemId(storageUnit.id(), item.getId()));
  }

  @Test
  void testComplexWarehouseScenario() {
    // Create warehouse with multiple storage units
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Complex Warehouse", "Complex Address");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto unitsA =
        storageUnitService.createStorageUnit("ZONE-A", warehouse.id(), 100);
    StorageUnitResponseDto unitsB =
        storageUnitService.createStorageUnit("ZONE-B", warehouse.id(), 150);
    StorageUnitResponseDto unitsC =
        storageUnitService.createStorageUnit("ZONE-C", warehouse.id(), 80);

    // Create various inventory items
    InventoryItem productA = new InventoryItem(owner, "PROD-A", "Product A", 200);
    InventoryItem productB = new InventoryItem(owner, "PROD-B", "Product B", 150);
    InventoryItem productC = new InventoryItem(owner, "PROD-C", "Product C", 100);

    productA = inventoryRepository.save(productA);
    productB = inventoryRepository.save(productB);
    productC = inventoryRepository.save(productC);

    // Distribute inventory across zones
    assignmentService.createStorageUnitInventoryAssignment(unitsA.id(), productA.getId(), 80);
    assignmentService.createStorageUnitInventoryAssignment(unitsB.id(), productA.getId(), 100);
    assignmentService.createStorageUnitInventoryAssignment(unitsB.id(), productB.getId(), 150);
    assignmentService.createStorageUnitInventoryAssignment(unitsC.id(), productC.getId(), 80);

    // Verify zone A has one assignment
    List<StorageUnitInventoryAssignmentDto> zoneA =
        assignmentService.getByStorageUnitId(unitsA.id());
    assertEquals(1, zoneA.size());

    // Verify zone B has two assignments
    List<StorageUnitInventoryAssignmentDto> zoneB =
        assignmentService.getByStorageUnitId(unitsB.id());
    assertEquals(2, zoneB.size());

    // Verify zone C has one assignment
    List<StorageUnitInventoryAssignmentDto> zoneC =
        assignmentService.getByStorageUnitId(unitsC.id());
    assertEquals(1, zoneC.size());
  }

  @Test
  void testMultipleStorageUnitsInSameWarehouse() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Multi-Unit Warehouse", "Multi Address");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto unit1 =
        storageUnitService.createStorageUnit("RACK-1", warehouse.id(), 100);
    StorageUnitResponseDto unit2 =
        storageUnitService.createStorageUnit("RACK-2", warehouse.id(), 100);
    StorageUnitResponseDto unit3 =
        storageUnitService.createStorageUnit("RACK-3", warehouse.id(), 100);

    // Verify all units created
    List<StorageUnitResponseDto> units =
        storageUnitService.getStorageUnitsByWarehouseId(warehouse.id());
    assertEquals(3, units.size());
  }

  @Test
  void testInventoryItemWithZeroInitialQuantity() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Zero-Quantity Warehouse", "Zero Address");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit("UNIT-ZERO", warehouse.id(), 50);

    // Create inventory item with 0 quantity
    InventoryItem item = new InventoryItem(owner, "SKU-ZERO", "Zero Item", 0);
    item = inventoryRepository.save(item);

    // Can still assign it with minimum quantity of 1
    StorageUnitInventoryAssignmentDto assignment =
        assignmentService.createStorageUnitInventoryAssignment(storageUnit.id(), item.getId(), 1);

    assertNotNull(assignment);
    assertEquals(1, assignment.assignedQuantity());
  }

  @Test
  void testStorageUnitByCode() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Code Warehouse", "Code Address");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto created =
        storageUnitService.createStorageUnit("UNIQUE-CODE-123", warehouse.id(), 100);

    StorageUnitResponseDto retrieved = storageUnitService.getStorageUnitByCode("UNIQUE-CODE-123");

    assertEquals(created.id(), retrieved.id());
    assertEquals("UNIQUE-CODE-123", retrieved.code());
  }

  @Test
  void testStorageUnitByWarehouseIdAndCode() {
    WarehouseResponseDto warehouse =
        warehouseService.createWarehouse(owner, "Combined Lookup", "Combined Address");

    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    StorageUnitResponseDto created =
        storageUnitService.createStorageUnit("UNIT-COMBINED", warehouse.id(), 75);

    StorageUnitResponseDto retrieved =
        storageUnitService.getStorageUnitByWarehouseIdAndCode(warehouse.id(), "UNIT-COMBINED");

    assertEquals(created.id(), retrieved.id());
    assertEquals("UNIT-COMBINED", retrieved.code());
  }
}
