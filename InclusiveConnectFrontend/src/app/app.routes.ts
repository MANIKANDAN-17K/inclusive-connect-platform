import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/landing/landing.component').then((m) => m.LandingComponent),
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/candidate-profile/profile.component').then((m) => m.ProfileComponent),
  },
  {
  path: 'company',
  canActivate: [authGuard],
  loadComponent: () =>
    import('./features/profile/company-profile/company-profile.component').then((m) => m.CompanyProfileComponent),
  },
  {
  path: 'jobs',
  loadComponent: () => import('./features/jobs/job-list/job-list.component').then((m) => m.JobListComponent),
},
{
  path: 'jobs/:id',
  loadComponent: () => import('./features/jobs/job-detail/job_detail.component').then((m) => m.JobDetailComponent),
},
  {
    path: '**',
    loadComponent: () =>
      import('./pages/not-found/not-found.component').then((m) => m.NotFoundComponent),
  },
];