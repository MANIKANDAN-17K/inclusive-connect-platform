import { Component, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { NetworkService, NetworkUser, NetworkUsersPage } from '../../../core/services/network.service';
import { ConnectionService } from '../../../core/services/connection.service';
import { NetworkUserCardComponent } from '../network-user-card/network-user-card.component';

@Component({
    selector: 'ic-search-users',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, NetworkUserCardComponent],
    template: `
    <div class="px-6 py-4">
      <!-- Search bar -->
      <div class="relative mb-6">
        <label for="search-input" class="sr-only">Search people</label>
        <div class="absolute inset-y-0 left-4 flex items-center pointer-events-none">
          <svg class="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
        </div>
        <input
          id="search-input"
          type="search"
          [formControl]="searchControl"
          placeholder="Search by name, headline, or skill…"
          autocomplete="off"
          aria-label="Search people by name, headline, or skill"
          class="w-full pl-12 pr-4 py-3 rounded-2xl border border-gray-200 bg-white text-sm shadow-sm
                 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent transition-shadow"
        />
        <div *ngIf="searching"
             class="absolute inset-y-0 right-4 flex items-center">
          <svg class="w-4 h-4 text-indigo-500 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
          </svg>
        </div>
      </div>

      <!-- Initial prompt -->
      <div *ngIf="!keyword && !users.length && !searching"
           class="flex flex-col items-center justify-center py-16 text-gray-400 gap-3">
        <svg class="w-14 h-14 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
        <p class="font-semibold">Search for people</p>
        <p class="text-sm">Try searching by a name, job title, or skill like "React" or "Java".</p>
      </div>

      <!-- Empty results -->
      <div *ngIf="keyword && !searching && users.length === 0"
           class="flex flex-col items-center justify-center py-16 text-gray-400 gap-3">
        <p class="font-semibold">No results for "{{ keyword }}"</p>
        <p class="text-sm">Try a different keyword.</p>
      </div>

      <!-- Skeleton -->
      <div *ngIf="searching" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div *ngFor="let _ of skeletons" class="h-44 bg-gray-100 rounded-2xl animate-pulse"></div>
      </div>

      <!-- Results -->
      <div *ngIf="!searching && users.length" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <ic-network-user-card
          *ngFor="let user of users"
          [user]="user"
          [showConnect]="true"
          [connecting]="connectingId === user.id"
          (connect)="sendRequest(user)"
        />
      </div>
    </div>
  `,
})
export class SearchUsersComponent implements OnDestroy {
    private networkService = inject(NetworkService);
    private connectionService = inject(ConnectionService);

    searchControl = new FormControl('');
    users: NetworkUser[] = [];
    page: NetworkUsersPage | null = null;
    searching = false;
    connectingId: number | null = null;
    keyword = '';
    skeletons = Array(6);

    private sub: Subscription;

    constructor() {
        this.sub = this.searchControl.valueChanges.pipe(
            debounceTime(400),
            distinctUntilChanged(),
            switchMap((kw) => {
                this.keyword = kw?.trim() ?? '';
                if (!this.keyword) {
                    this.users = [];
                    this.searching = false;
                    return [];
                }
                this.searching = true;
                return this.networkService.searchUsers(this.keyword, 0, 12);
            }),
        ).subscribe({
            next: (res) => {
                this.page = res?.data ?? null;
                this.users = this.page?.content ?? [];
                this.searching = false;
            },
            error: () => { this.searching = false; },
        });
    }

    sendRequest(user: NetworkUser): void {
        if (user.requestPending || user.alreadyConnected || this.connectingId === user.id) return;
        this.connectingId = user.id;
        this.connectionService.sendRequest(user.id).subscribe({
            next: () => { user.requestPending = true; this.connectingId = null; },
            error: () => { this.connectingId = null; },
        });
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }
}
