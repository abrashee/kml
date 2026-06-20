// src / app / features/ user /models/ users.model.ts
export type UserRole = 'ADMIN' | 'MANAGER' | 'WORKER' | 'CUSTOMER';

export interface User {
  id: number;
  username: string;
  name: string;
  role: UserRole;
  createdAt: string;
  warehouseId?: number;
  storageId?: number;
  avatarUrl?: string;
  address?: string;
}

// Optional: You could create a specific interface for logistics users
export interface LogisticsUser extends User {
  warehouseId?: number;
  storageId?: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface CreateUserRequest {
  username: string;
  password: string;
  name: string;
  userRole: UserRole;
}