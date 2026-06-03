import { Component, inject, signal, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { OrderStatus, OrderItem, CreateOrderRequest } from '../../core/models/order.model';

@Component({
  selector: 'app-order-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h1>{{ isEdit() ? 'Edit' : 'New' }} Order</h1>
    </div>

    <div class="card" style="max-width: 720px">
      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-group">
          <label class="form-label">Order Code *</label>
          <input class="form-control" [class.invalid]="isInvalid('code')" formControlName="code" />
          @if (isInvalid('code')) { <div class="form-error">Code is required</div> }
        </div>

        <div class="form-group">
          <label class="form-label">Status *</label>
          <select class="form-control" formControlName="statusId">
            <option value="">— Select status —</option>
            @for (s of statuses(); track s.id) {
              <option [value]="s.id">{{ s.name }}</option>
            }
          </select>
        </div>

        <div class="form-group">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
            <label class="form-label" style="margin:0">Items</label>
            <button type="button" class="btn btn-outline btn-sm" (click)="addItem()">+ Add Item</button>
          </div>
          <div formArrayName="items">
            @for (item of itemsArray.controls; track $index) {
              <div [formGroupName]="$index"
                   style="display:grid;grid-template-columns:1fr 1fr 1fr auto;gap:8px;margin-bottom:8px;align-items:center">
                <input type="number" class="form-control" formControlName="inventoryItemId" placeholder="Item ID" min="1" />
                <input type="number" class="form-control" formControlName="quantity" placeholder="Qty" min="1" />
                <input type="number" class="form-control" formControlName="unitPrice" placeholder="Unit Price" min="0" step="0.01" />
                <button type="button" class="btn btn-danger btn-sm" (click)="removeItem($index)">✕</button>
              </div>
            }
          </div>
        </div>

        <div style="display:flex;gap:8px;margin-top:24px">
          <button type="submit" class="btn btn-primary" [disabled]="loading() || form.invalid">
            {{ loading() ? 'Saving…' : 'Save' }}
          </button>
          <a routerLink="/orders" class="btn btn-outline">Cancel</a>
        </div>
      </form>
    </div>
  `
})
export class OrderFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(OrderService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  loading = signal(false);
  isEdit = signal(false);
  statuses = signal<OrderStatus[]>([]);
  private orderId: number | null = null;

  form = this.fb.group({
    code:     ['', Validators.required],
    statusId: [null as number | null, Validators.required],
    items:    this.fb.array<ReturnType<typeof this.createItemGroup>>([])
  });

  get itemsArray(): FormArray { return this.form.get('items') as FormArray; }

  private createItemGroup(item?: Partial<OrderItem>) {
    return this.fb.group({
      inventoryItemId: [item?.inventoryItemId ?? null, Validators.required],
      quantity:  [item?.quantity  ?? 1, [Validators.required, Validators.min(1)]],
      unitPrice: [item?.unitPrice ?? 0, [Validators.required, Validators.min(0)]]
    });
  }

  isInvalid(field: string): boolean {
    const c = this.form.get(field);
    return !!(c?.invalid && c?.touched);
  }

  addItem(): void { this.itemsArray.push(this.createItemGroup()); }

  removeItem(index: number): void { this.itemsArray.removeAt(index); }

  ngOnInit(): void {
    this.service.getStatuses().subscribe(s => this.statuses.set(s));

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit.set(true);
      this.orderId = +id;
      this.service.getById(+id).subscribe(order => {
        this.form.patchValue({ code: order.code, statusId: order.items[0]?.id ?? null });
        order.items.forEach(item => this.itemsArray.push(this.createItemGroup(item)));
      });
    } else {
      this.addItem();
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    const val = this.form.value;
    const req: CreateOrderRequest = {
      code:     val.code!,
      statusId: val.statusId!,
      items:    (val.items as OrderItem[]) ?? []
    };

    const obs = this.isEdit() && this.orderId !== null
      ? this.service.update(this.orderId, req)
      : this.service.create(req);

    obs.subscribe({
      next: () => this.router.navigate(['/orders']),
      error: () => this.loading.set(false)
    });
  }
}
