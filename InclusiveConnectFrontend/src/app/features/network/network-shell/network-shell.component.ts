import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { IcIconName } from '../../../shared/icons/icons';

interface NavTab {
  label:  string;
  path:   string;
  id:     string;
  icon:   IcIconName;
  exact?: boolean;
}

@Component({
  selector: 'ic-network-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, IconComponent],
  template: `
    <div class="page-shell animate-fade-in">

      <!-- Page header -->
      <div class="mb-6">
        <h1 class="section-title">My Network</h1>
        <p class="text-sm text-text-secondary mt-1">
          Discover professionals, manage connections and requests
        </p>
      </div>

      <!-- Tab bar -->
      <nav
        class="flex gap-1 mb-7 border-b border-border"
        aria-label="Network sections"
        role="tablist"
      >
        <a
          *ngFor="let tab of tabs"
          [routerLink]="tab.path"
          routerLinkActive="tab-active"
          [routerLinkActiveOptions]="{ exact: !!tab.exact }"
          [id]="tab.id"
          role="tab"
          class="tab-link"
          [attr.aria-selected]="false"
        >
          <ic-icon [name]="tab.icon" [size]="16"></ic-icon>
          <span>{{ tab.label }}</span>
        </a>
      </nav>

      <!-- Routed content -->
      <router-outlet />
    </div>
  `,
  styles: [`
    .tab-link {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 8px 14px;
      font-size: 0.875rem;
      font-weight: 500;
      color: var(--color-text-secondary);
      border-bottom: 2px solid transparent;
      margin-bottom: -1px;
      transition: color 150ms, border-color 150ms;
      white-space: nowrap;
      border-radius: 6px 6px 0 0;
    }
    .tab-link:hover {
      color: var(--color-primary);
      background: #f8fafc;
    }
    .tab-link:focus-visible {
      outline: 2px solid var(--color-primary);
      outline-offset: 2px;
    }
    .tab-active {
      color: var(--color-primary) !important;
      border-bottom-color: var(--color-primary) !important;
      font-weight: 600;
    }
  `],
})
export class NetworkShellComponent {
  readonly tabs: NavTab[] = [
    { label: 'Discover',     path: '/network/discover',     id: 'tab-discover',     icon: 'users' },
    { label: 'Search',       path: '/network/search',       id: 'tab-search',       icon: 'search' },
    { label: 'Connections',  path: '/network/connections',  id: 'tab-connections',  icon: 'network' },
    { label: 'Pending',      path: '/network/pending',      id: 'tab-pending',      icon: 'bell' },
    { label: 'Sent',         path: '/network/sent',         id: 'tab-sent',         icon: 'send' },
  ];
}
