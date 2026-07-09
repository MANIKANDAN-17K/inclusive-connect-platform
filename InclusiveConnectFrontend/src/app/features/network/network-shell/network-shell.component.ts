import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

interface NavTab {
    label: string;
    path: string;
    id: string;
}

@Component({
    selector: 'ic-network-shell',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
    template: `
    <div class="max-w-6xl mx-auto px-4 py-6">
      <!-- Page header -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900">My Network</h1>
        <p class="text-sm text-gray-500 mt-1">Discover professionals, manage your connections and requests</p>
      </div>

      <!-- Tab navigation -->
      <nav class="flex gap-1 mb-6 bg-gray-100 p-1 rounded-2xl w-fit" aria-label="Network sections">
        <a
          *ngFor="let tab of tabs"
          [routerLink]="tab.path"
          routerLinkActive="bg-white shadow-sm text-gray-900 font-semibold"
          [routerLinkActiveOptions]="{ exact: true }"
          [id]="tab.id"
          [attr.aria-label]="tab.label"
          class="px-4 py-2 rounded-xl text-sm transition-all duration-150 focus:outline-none
                 focus:ring-2 focus:ring-indigo-400 text-gray-500 hover:text-gray-700 cursor-pointer whitespace-nowrap"
        >
          {{ tab.label }}
        </a>
      </nav>

      <!-- Routed child component -->
      <router-outlet />
    </div>
  `,
})
export class NetworkShellComponent {
    private router = inject(Router);

    tabs: NavTab[] = [
        { label: '🔍 Discover', path: '/network/discover', id: 'tab-discover' },
        { label: '🔎 Search', path: '/network/search', id: 'tab-search' },
        { label: '🤝 Connections', path: '/network/connections', id: 'tab-connections' },
        { label: '📥 Pending', path: '/network/pending', id: 'tab-pending' },
        { label: '📤 Sent', path: '/network/sent', id: 'tab-sent' },
    ];
}
