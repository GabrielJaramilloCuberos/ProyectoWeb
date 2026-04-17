import { Routes } from '@angular/router';

export const COORDINATOR_ROUTES: Routes = [
	{
		path: '',
		pathMatch: 'full',
		redirectTo: 'home'
	},
	{
		path: 'home',
		loadComponent: () =>
			import('./pages/home/home').then(m => m.Home)
	}
];

