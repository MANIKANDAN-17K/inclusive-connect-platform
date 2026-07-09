import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

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
    path: 'applications',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/jobs/my-applications/my-applications.component').then((m) => m.MyApplicationsComponent),
  },
  {
    path: 'chat',
    canActivate: [authGuard],
    loadComponent: () => import('./features/chat/chat.component').then((m) => m.ChatComponent),
  },
  {
    path: 'network',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/network/network-shell/network-shell.component').then((m) => m.NetworkShellComponent),
    children: [
      { path: '', redirectTo: 'discover', pathMatch: 'full' },
      {
        path: 'discover',
        loadComponent: () =>
          import('./features/network/discover-users/discover-users.component').then((m) => m.DiscoverUsersComponent),
      },
      {
        path: 'search',
        loadComponent: () =>
          import('./features/network/search-users/search-users.component').then((m) => m.SearchUsersComponent),
      },
      {
        path: 'connections',
        loadComponent: () =>
          import('./features/network/connections/connections.component').then((m) => m.ConnectionsComponent),
      },
      {
        path: 'pending',
        loadComponent: () =>
          import('./features/network/pending-requests/pending-requests.component').then((m) => m.PendingRequestsComponent),
      },
      {
        path: 'sent',
        loadComponent: () =>
          import('./features/network/sent-requests/sent-requests.component').then((m) => m.SentRequestsComponent),
      },
    ],
  },
  {
    path: 'notifications',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/notifications/notifications.component').then(
        (m) => m.NotificationsComponent
      ),
  },
  {
    path: 'accessibility',
    loadComponent: () =>
      import('./features/accessibility/accessibility-setting.component').then(
        (m) => m.AccessibilitySettingsComponent
      ),
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/admin-dashboard/admin-dashboard.component').then((m) => m.AdminDashboardComponent),
  },
  {
    path: 'admin/users',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/admin-user/admin-users.component').then((m) => m.AdminUsersComponent),
  },
  {
    path: 'admin/employers',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/admin-employers/admin-employers.component').then((m) => m.AdminEmployersComponent),
  },
  {
    path: '**',
    loadComponent: () =>
      import('./pages/not-found/not-found.component').then((m) => m.NotFoundComponent),
  },
];