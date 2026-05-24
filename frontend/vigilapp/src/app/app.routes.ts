import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'profesor',
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: 'PROFESOR' },
    loadChildren: () =>
      import('./features/profesor/profesor.routes').then(m => m.PROFESOR_ROUTES)
  },
  {
    path: 'coordinator',
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: 'COORDINADOR' },
    loadChildren: () =>
      import('./features/coordinator/coordinator.routes').then(
        m => m.COORDINATOR_ROUTES
      )
  },
  {
    path: 'administrator',
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: 'ADMINISTRADOR' },
    loadChildren: () =>
      import('./features/administrator/administrator.routes').then(
        m => m.ADMINISTRATOR_ROUTES
      )
  }
];
