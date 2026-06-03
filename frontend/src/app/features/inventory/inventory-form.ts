import { Component, inject, signal, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { InventoryService } from '../../core/services/inventory.service';
import { CreateInventoryRequest } from '../../core/models/inventory.model';

@Component({
  selector: 'app-inventory-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-header">
      <h1>{{ isEdit() ? 'Edit' : 'New' }} Inventory Item</h1>
    </div>

    <div class="card" style="max-width: 600px">
      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-group">
          <label class="form-label">SKU *</label>
          <input class="form-control" [class.invalid]="isInvalid('sku')" formControlName="sku" />
          @if (isInvalid('sku')) { <div class="form-error">SKU is required</div> }
        </div>

        <div class="form-group">
          <label class="form-label">Name *</label>
          <input class="form-control" [class.invalid]="isInvalid('name')" formControlName="name" />
          @if (isInvalid('name')) { <div class="form-error">Name is required</div> }
        </div>

        <div class="form-group">
          <label class="form-label">Quantity *</label>
          <input type="number" class="form-control" [class.invalid]="isInvalid('quantity')"
                 formControlName="quantity" min="0" />
          @if (isInvalid('quantity')) { <div class="form-error">Quantity must be 0 or more</div> }
        </div>

        <div style="display:flex;gap:8px;margin-top:24px">
          <button type="submit" class="btn btn-primary" [disabled]="loading() || form.invalid">
            {{ loading() ? 'Saving…' : 'Save' }}
          </button>
          <a routerLink="/inventory" class="btn btn-outline">Cancel</a>
        </div>
      </form>
    </div>
  `
})
export class InventoryFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(InventoryService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  loading = signal(false);
  isEdit = signal(false);
  private itemId: number | null = null;

  form = this.fb.group({
    sku:      ['', Validators.required],
    name:     ['', Validators.required],
    quantity: [0, [Validators.required, Validators.min(0)]]
  });

  isInvalid(field: string): boolean {
    const c = this.form.get(field);
    return !!(c?.invalid && c?.touched);
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit.set(true);
      this.itemId = +id;
      this.service.getById(+id).subscribe(item =>
        this.form.patchValue({ sku: item.sku, name: item.name, quantity: item.quantity })
      );
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    const val = this.form.value;
    const req: CreateInventoryRequest = {
      sku: val.sku!,
      name: val.name!,
      quantity: val.quantity!
    };

    const obs = this.isEdit() && this.itemId !== null
      ? this.service.update(this.itemId, req)
      : this.service.create(req);

    obs.subscribe({
      next: () => this.router.navigate(['/inventory']),
      error: () => this.loading.set(false)
    });
  }
}
