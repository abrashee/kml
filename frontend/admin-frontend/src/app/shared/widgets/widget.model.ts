// src/app/shared/widgets/widget.model.ts
import { Type } from '@angular/core';
import { Capability } from '../../core/models/capability.model';

export interface DashboardWidget {
  id: string;
  component: Type<any>;
  capabilities: Capability[];
}
