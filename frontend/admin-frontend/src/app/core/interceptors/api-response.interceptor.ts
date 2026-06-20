import { HttpEvent, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs';

type ApiEnvelope<T = unknown> = {
  success: boolean;
  data: T;
  message?: string;
  timestamp?: string;
};

const isApiEnvelope = (body: unknown): body is ApiEnvelope =>
  !!body &&
  typeof body === 'object' &&
  'success' in body &&
  'data' in body;

export const apiResponseInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    map((event: HttpEvent<unknown>) => {
      if (event instanceof HttpResponse && isApiEnvelope(event.body)) {
        return event.clone({ body: event.body.data });
      }

      return event;
    })
  );
};
