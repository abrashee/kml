// // src / utils / queryString.ts
export function toQuery(params: Record<string, any>) {
  return new URLSearchParams(params).toString();
}