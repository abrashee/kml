package com.kml.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kml.api.error.OwnershipException;
import com.kml.capacity.dto.warehouse.WarehouseResponseDto;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.WarehouseService;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.UserRepository;
import com.kml.infra.WarehouseRepository;
import java.util.List;
import java.util.Optional;
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
class WarehouseServiceIntegrationTest {

  @Autowired private WarehouseService warehouseService;
  @Autowired private WarehouseRepository warehouseRepository;
  @Autowired private UserRepository userRepository;
  @MockBean private CurrentUserProvider currentUserProvider;

  private User owner;
  private User anotherUser;

  @BeforeEach
  void setup() {
    // Create test users
    owner = new User("Warehouse Owner", "owner", "password123", UserRole.USER);
    anotherUser = new User("Another User", "anotheruser", "password456", UserRole.USER);

    owner = userRepository.save(owner);
    anotherUser = userRepository.save(anotherUser);
  }

  @Test
  void testCreateWarehouseSuccessfully() {
    String warehouseName = "Main Distribution Center";
    String address = "123 Main Street, New York, NY 10001";

    WarehouseResponseDto created = warehouseService.createWarehouse(owner, warehouseName, address);

    assertNotNull(created);
    assertNotNull(created.id());
    assertEquals(warehouseName, created.name());
    assertEquals(address, created.address());
  }

  @Test
  void testCreateWarehouseWithNullName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> warehouseService.createWarehouse(owner, null, "123 Main Street"));
  }

  @Test
  void testCreateWarehouseWithBlankName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> warehouseService.createWarehouse(owner, "   ", "123 Main Street"));
  }

  @Test
  void testCreateWarehouseWithNullAddress() {
    assertThrows(
        IllegalArgumentException.class,
        () -> warehouseService.createWarehouse(owner, "Main Warehouse", null));
  }

  @Test
  void testCreateWarehouseWithBlankAddress() {
    assertThrows(
        IllegalArgumentException.class,
        () -> warehouseService.createWarehouse(owner, "Main Warehouse", "   "));
  }

  @Test
  void testCreateWarehouseWithNullOwner() {
    assertThrows(
        IllegalArgumentException.class,
        () -> warehouseService.createWarehouse(null, "Main Warehouse", "123 Main Street"));
  }

  @Test
  void testGetWarehouseById() {
    // Create a warehouse
    WarehouseResponseDto created =
        warehouseService.createWarehouse(
            owner, "Warehouse A", "456 Oak Avenue, Los Angeles, CA 90001");

    // Mock the current user provider to return the owner
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    // Retrieve the warehouse
    Optional<WarehouseResponseDto> found = warehouseService.getWarehouseById(created.id());

    assertTrue(found.isPresent());
    assertEquals(created.id(), found.get().id());
    assertEquals("Warehouse A", found.get().name());
    assertEquals("456 Oak Avenue, Los Angeles, CA 90001", found.get().address());
  }

  @Test
  void testGetWarehouseByNonExistentId() {
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    assertThrows(IllegalArgumentException.class, () -> warehouseService.getWarehouseById(99999L));
  }

  @Test
  void testGetWarehouseByName() {
    // Create a warehouse
    WarehouseResponseDto created =
        warehouseService.createWarehouse(
            owner, "West Coast Warehouse", "789 Pine Street, Seattle, WA 98101");

    // Mock the current user provider
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    // Retrieve by name
    Optional<WarehouseResponseDto> found =
        warehouseService.getWarehouseByName("West Coast Warehouse");

    assertTrue(found.isPresent());
    assertEquals("West Coast Warehouse", found.get().name());
    assertEquals("789 Pine Street, Seattle, WA 98101", found.get().address());
  }

  @Test
  void testGetWarehouseByNonExistentName() {
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    assertThrows(
        IllegalArgumentException.class,
        () -> warehouseService.getWarehouseByName("Non-existent Warehouse"));
  }

  @Test
  void testGetAllWarehouses() {
    // Create multiple warehouses
    WarehouseResponseDto warehouse1 =
        warehouseService.createWarehouse(owner, "Warehouse 1", "Address 1");
    WarehouseResponseDto warehouse2 =
        warehouseService.createWarehouse(owner, "Warehouse 2", "Address 2");

    // Mock the current user provider
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    // Retrieve all warehouses for the owner
    List<WarehouseResponseDto> warehouses = warehouseService.getAllWarehouses();

    assertEquals(2, warehouses.size());
    assertTrue(warehouses.stream().anyMatch(w -> w.name().equals("Warehouse 1")));
    assertTrue(warehouses.stream().anyMatch(w -> w.name().equals("Warehouse 2")));
  }

  @Test
  void testGetAllWarehousesFiltersOtherUserWarehouses() {
    // Create warehouses for different owners
    warehouseService.createWarehouse(owner, "Owner Warehouse", "Owner Address");
    warehouseService.createWarehouse(anotherUser, "Another User Warehouse", "Another Address");

    // Mock the current user provider to return the first owner
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    // Retrieve all warehouses
    List<WarehouseResponseDto> warehouses = warehouseService.getAllWarehouses();

    assertEquals(1, warehouses.size());
    assertEquals("Owner Warehouse", warehouses.get(0).name());
  }

  @Test
  void testEnforceOwnershipSucceedsForOwner() {
    // Create a warehouse
    WarehouseResponseDto created =
        warehouseService.createWarehouse(owner, "Owner's Warehouse", "Owner Address");

    // Should not throw an exception
    warehouseService.enforceOwnership(created.id(), owner);
  }

  @Test
  void testEnforceOwnershipFailsForNonOwner() {
    // Create a warehouse owned by 'owner'
    WarehouseResponseDto created =
        warehouseService.createWarehouse(owner, "Owner's Warehouse", "Owner Address");

    // Try to access with different user
    assertThrows(
        OwnershipException.class,
        () -> warehouseService.enforceOwnership(created.id(), anotherUser));
  }

  @Test
  void testEnforceOwnershipFailsForNonExistentWarehouse() {
    assertThrows(
        IllegalArgumentException.class, () -> warehouseService.enforceOwnership(99999L, owner));
  }

  @Test
  void testCreateMultipleWarehousesAndRetrieveThem() {
    // Create warehouses
    WarehouseResponseDto w1 =
        warehouseService.createWarehouse(owner, "Warehouse Alpha", "Address Alpha");
    WarehouseResponseDto w2 =
        warehouseService.createWarehouse(owner, "Warehouse Beta", "Address Beta");
    WarehouseResponseDto w3 =
        warehouseService.createWarehouse(owner, "Warehouse Gamma", "Address Gamma");

    // Mock current user
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);

    // Verify we can retrieve all
    List<WarehouseResponseDto> allWarehouses = warehouseService.getAllWarehouses();
    assertEquals(3, allWarehouses.size());

    // Verify each one
    org.mockito.Mockito.when(currentUserProvider.getCurrentUser()).thenReturn(owner);
    assertTrue(warehouseService.getWarehouseById(w1.id()).isPresent());
    assertTrue(warehouseService.getWarehouseById(w2.id()).isPresent());
    assertTrue(warehouseService.getWarehouseById(w3.id()).isPresent());
  }

  @Test
  void testWarehouseIsPersistetToDatabase() {
    // Create a warehouse
    WarehouseResponseDto created =
        warehouseService.createWarehouse(owner, "Persistent Warehouse", "Persistent Address");

    // Verify it exists in the repository
    Optional<Warehouse> found = warehouseRepository.findByName("Persistent Warehouse");
    assertTrue(found.isPresent());
    assertEquals(created.id(), found.get().getId());
    assertEquals("Persistent Address", found.get().getAddress());
  }
}
