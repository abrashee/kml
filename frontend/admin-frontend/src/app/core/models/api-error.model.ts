// // src/ app / core / model / api-error.model.ts
export interface ApiError {
  status: number;
  error: string;
  message: string;
  path: string;
}
