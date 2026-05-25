import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { provideClientHydration, withEventReplay, withHttpTransferCacheOptions } from '@angular/platform-browser';

import { routes } from './app.routes';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';
import { RetryInterceptor } from './core/interceptors/retry.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    provideClientHydration(
      withEventReplay(),
      // Desactiva el cache HTTP transferido del SSR: las peticiones autenticadas
      // (que requieren token JWT del navegador) deben re-ejecutarse en el cliente.
      withHttpTransferCacheOptions({ filter: () => false })
    ),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor,   multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: RetryInterceptor, multi: true }
  ]
};