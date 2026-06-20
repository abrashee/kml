// src/app/features/users/models/staff.model.ts
import { UserRole } from './user.model';
export interface StaffMember {
  id: number;
  username: string; // Read-only after creation
  name: string;     // Read-only after creation
  role: UserRole;
  status: 'ACTIVE' | 'SUSPENDED';
  createdAt: string;

  warehouseId?: number;
  warehouseName?: string;
  storageId?: number;
  managerId?: number;
  avatarUrl?: string;
}

export interface CreateStaffRequest {
  username: string;
  name: string;
  password: string; // Temporary initial password
  role: UserRole;
  warehouseId?: number;
}

export interface UpdateStaffRoleRequest {
  role: UserRole;
  warehouseId?: number;
  status: 'ACTIVE' | 'SUSPENDED';
}
