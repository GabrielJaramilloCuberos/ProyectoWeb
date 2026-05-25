import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { retry } from 'rxjs/operators';

/**
 * Interceptor global de reintentos.
 *
 * Reintenta automáticamente las peticiones que fallan por errores transitorios
 * (problemas de red o errores 5xx del servidor) con un backoff exponencial.
 *
 * Comportamiento:
 *  - NO reintenta peticiones POST/PUT/PATCH/DELETE: si el servidor ya las procesó
 *    pero falló al responder, un reintento causaría duplicados/efectos no deseados.
 *  - NO reintenta errores 4xx (auth, validación, recurso no encontrado): son culpa
 *    del cliente y reintentar no los va a arreglar.
 *  - SÍ reintenta hasta 3 veces los GET con errores de red (status 0) o 5xx.
 *  - Backoff exponencial: 500ms, 1s, 2s entre intentos para no saturar al backend.
 */
@Injectable()
export class RetryInterceptor implements HttpInterceptor {
  private readonly MAX_RETRIES   = 3;
  private readonly INITIAL_DELAY = 500; // ms

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Solo reintentamos métodos idempotentes (GET/HEAD/OPTIONS).
    if (!this.esIdempotente(req.method)) {
      return next.handle(req);
    }

    return next.handle(req).pipe(
      retry({
        count: this.MAX_RETRIES,
        delay: (error: HttpErrorResponse, retryCount: number) => {
          if (!this.esReintentable(error)) {
            // Propaga el error sin reintentar
            return throwError(() => error);
          }
          // Backoff exponencial: 500ms, 1s, 2s, ...
          const espera = this.INITIAL_DELAY * Math.pow(2, retryCount - 1);
          console.warn(
            `[RetryInterceptor] ${req.method} ${req.url} falló (status ${error.status}). ` +
            `Reintento ${retryCount}/${this.MAX_RETRIES} en ${espera}ms`
          );
          return timer(espera);
        },
      })
    );
  }

  private esIdempotente(metodo: string): boolean {
    return ['GET', 'HEAD', 'OPTIONS'].includes(metodo.toUpperCase());
  }

  private esReintentable(error: HttpErrorResponse): boolean {
    // status 0     → error de red (CORS, sin conexión, DNS, etc.)
    // status 5xx   → error del servidor (probablemente transitorio)
    // status 4xx   → error del cliente: NO reintentar (auth, validación, 404, etc.)
    return error.status === 0 || (error.status >= 500 && error.status < 600);
  }
}
