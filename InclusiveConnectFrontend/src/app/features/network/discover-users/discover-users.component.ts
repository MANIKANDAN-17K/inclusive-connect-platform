import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NetworkService, NetworkUser, NetworkUsersPage } from '../../../core/services/network.service';
import { ConnectionService } from '../../../core/services/connection.service';
import { NetworkUserCardComponent } from '../network-user-card/network-user-card.component';

@Component({
    selector: 'ic-discover-users',
    standalone: true,
    imports: [CommonModule, NetworkUserCardComponent],
    template: `
    <div class="px-6 py-4">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-bold text-gray-900">
          People You May Know
          <span *ngIf="page" class="ml-2 text-sm font-normal text-gray-400">
            ({{ page.totalElements }} people)
          </span>
        </h2>
      </div>

      <!-- Skeleton loading grid -->
      <div *ngIf="loading" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div *ngFor="let _ of skeletons"
             class="h-44 bg-gray-100 rounded-2xl animate-pulse"></div>
      </div>

      <!-- Empty state -->
      <div *ngIf="!loading && users.length === 0"
           class="flex flex-col items-center justify-center py-20 text-gray-400 gap-3">
        <svg class="w-14 h-14 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
        <p class="font-semibold">No users to discover</p>
        <p class="text-sm">You're already connected with everyone, or there are no active users yet.</p>
      </div>

      <!-- User grid -->
      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <ic-network-user-card
          *ngFor="let user of users"
          [user]="user"
          [showConnect]="true"
          [connecting]="connectingId === user.id"
          (connect)="sendRequest(user)"
        />
      </div>

      <!-- Pagination -->
      <div *ngIf="page && !page.last" class="flex justify-center mt-8">
        <button
          (click)="loadMore()"
          id="load-more-btn"
          [disabled]="loadingMore"
          class="rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium
                 px-6 py-2.5 disabled:opacity-50 transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-400">
          {{ loadingMore ? 'Loading…' : 'Show More' }}
        </button>
      </div>
    </div>
  `,
})
export class DiscoverUsersComponent implements OnInit {
    private networkService = inject(NetworkService);
    private connectionService = inject(ConnectionService);

    users: NetworkUser[] = [];
    page: NetworkUsersPage | null = null;
    loading = true;
    loadingMore = false;
    connectingId: number | null = null;
    skeletons = Array(6);
    private currentPage = 0;

    ngOnInit(): void {
        this.loadPage(0);
    }

    private loadPage(pageNum: number): void {
        this.networkService.discoverPeople(pageNum, 12).subscribe({
            next: (res) => {
                this.page = res.data ?? null;
                if (pageNum === 0) {
                    this.users = this.page?.content ?? [];
                } else {
                    this.users = [...this.users, ...(this.page?.content ?? [])];
                }
                this.currentPage = pageNum;
                this.loading = false;
                this.loadingMore = false;
            },
            error: () => { this.loading = false; this.loadingMore = false; },
        });
    }

    loadMore(): void {
        this.loadingMore = true;
        this.loadPage(this.currentPage + 1);
    }

    sendRequest(user: NetworkUser): void {
        if (user.requestPending || user.alreadyConnected || this.connectingId === user.id) return;
        this.connectingId = user.id;
        this.connectionService.sendRequest(user.id).subscribe({
            next: () => {
                user.requestPending = true;
                this.connectingId = null;
            },
            error: () => { this.connectingId = null; },
        });
    }
}
