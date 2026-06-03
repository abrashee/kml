export type UserRole = 'ADMIN' | 'MANAGER' | 'USER' | 'CUSTOMER';

export interface User {
  id: number;
  username: string;
  name: string;
  role: UserRole;
  createdAt: string;
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