import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router, CanActivateFn } from '@angular/router';
import { Auth } from '../services/auth';

/**
 * Guard de autorización por rol.
 *
 * Verifica que el rol del usuario logueado coincida con el rol esperado
 * declarado en `route.data.expectedRole`.
 *
 * Uso en app.routes.ts:
 *   {
 *     path: 'administrator',
 *     canActivate: [authGuard, roleGuard],
 *     data: { expectedRole: 'ADMINISTRADOR' },
 *     ...
 *   }
 *
 * Si el rol no coincide:
 *  - Si el usuario tiene un rol válido pero distinto → lo manda al home de SU rol
 *    (mejor UX que cerrarle la sesión por meterse a una URL ajena).
 *  - Si no tiene rol o el rol es desconocido → lo manda a /login.
 *
 * Debe usarse siempre junto al authGuard (que valida que haya token).
 */
export const roleGuard: CanActivateFn = (route, _state) => {
  const auth       = inject(Auth);
  const router     = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // En SSR, permitir el renderizado inicial (será revalidado en el cliente)
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const expectedRole = route.data?.['expectedRole'] as string | undefined;
  const userRole     = auth.getRole();

  // Si la ruta no declara un rol esperado, no hay nada que validar
  if (!expectedRole) {
    return true;
  }

  // Coincidencia → acceso permitido
  if (userRole === expectedRole) {
    return true;
  }

  // No coincide → redirigir al home del rol real del usuario
  router.navigate([homeParaRol(userRole)]);
  return false;
};

/** Mapea cada rol a su ruta home. Si el rol es nulo o desconocido, manda al login. */
function homeParaRol(role: string | null): string {
  switch (role) {
    case 'PROFESOR':       return '/profesor/home';
    case 'COORDINADOR':    return '/coordinator/home';
    case 'ADMINISTRADOR':  return '/administrator/home';
    default:               return '/login';
  }
}
