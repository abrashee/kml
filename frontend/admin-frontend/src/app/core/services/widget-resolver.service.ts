// src/ app / core / widget-reseover.service.ts
import { Injectable } from '@angular/core';
import { ROLE_CAPABILITIES } from '../auth/role-capabilities';
import { WIDGET_REGISTRY } from '../../shared/widgets/widget.registry';
import { DashboardWidget } from '../../shared/widgets/widget.model';

@Injectable({ providedIn: 'root' })
export class WidgetResolverService {

  resolveWidgets(role: keyof typeof ROLE_CAPABILITIES): DashboardWidget[] {
    const capabilities = ROLE_CAPABILITIES[role] ?? [];

    return WIDGET_REGISTRY.filter(widget =>
      widget.capabilities.some(c => capabilities.includes(c))
    );
  }
}