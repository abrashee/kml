import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { InventoryDashboardComponent } from './inventory-dashboard';
import { InventoryService } from '../services/inventory.service';
import { WarehouseService } from '../../warehouses/services/warehouse.service';
import { AuthService } from '../../../core/auth/auth.service';

describe('InventoryDashboardComponent search debounce', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryDashboardComponent],
      providers: [
        { provide: InventoryService, useValue: { getInventory: jasmine.createSpy().and.returnValue({ subscribe: () => {} }), getStorageUnitsByWarehouse: jasmine.createSpy().and.returnValue({ subscribe: () => {} }), createItem: jasmine.createSpy(), updateQuantity: jasmine.createSpy(), deleteItem: jasmine.createSpy() } },
        { provide: WarehouseService, useValue: { getAll: jasmine.createSpy().and.returnValue({ subscribe: () => {} }), getStorageUnits: jasmine.createSpy().and.returnValue({ subscribe: () => {} }) } },
        { provide: AuthService, useValue: { currentUser: () => ({ role: 'ADMIN' }) } }
      ]
    }).compileComponents();
  });

  it('debounces inventory reload calls', fakeAsync(() => {
    const fixture = TestBed.createComponent(InventoryDashboardComponent);
    const component = fixture.componentInstance;
    const loadSpy = spyOn(component, 'loadInventory');

    component.ngOnInit();
    loadSpy.calls.reset();
    component.searchChanged('a');
    component.searchChanged('ab');
    component.searchChanged('abc');

    tick(299);
    expect(loadSpy).not.toHaveBeenCalled();

    tick(1);
    expect(loadSpy).toHaveBeenCalledTimes(1);
  }));
});
