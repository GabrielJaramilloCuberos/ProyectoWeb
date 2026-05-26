import { Routes } from '@angular/router';

export const COORDINATOR_ROUTES: Routes = [
	{
		path: '',
		pathMatch: 'full',
		redirectTo: 'dashboard'
	},
	{
		path: 'dashboard',
		loadComponent: () =>
			import('./pages/dashboard/dashboard').then(m => m.CoordinatorDashboard)
	},
	{
		path: 'home',
		pathMatch: 'full',
		redirectTo: 'live'
	},
	{
		path: 'live',
		loadComponent: () =>
			import('./pages/home/home').then(m => m.Home)
	},
	{
		path: 'analytics',
		loadComponent: () =>
			import('./pages/analytics/analytics').then(m => m.CoordinatorAnalytics)
	}
];

