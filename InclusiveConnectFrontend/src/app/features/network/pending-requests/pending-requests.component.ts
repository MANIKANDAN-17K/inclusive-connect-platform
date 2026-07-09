import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConnectionService, ConnectionRequest } from '../../../core/services/connection.service';

@Component({
  selector: 'ic-pending-requests',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="px-6 py-4">
      <h2 class="text-lg font-bold text-gray-900 mb-4">
        Pending Requests
        <span class="ml-2 text-sm font-normal text-gray-400">({{ requests.length }})</span>
      </h2>

      <div *ngIf="loading" class="space-y-3">
        <div *ngFor="let _ of skeletons" class="h-20 bg-gray-100 rounded-2xl animate-pulse"></div>
      </div>

      <div *ngIf="!loading && !requests.length"
           class="flex flex-col items-center justify-center py-20 text-gray-400 gap-3">
        <svg class="w-14 h-14 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/>
        </svg>
        <p class="font-semibold">No pending requests</p>
        <p class="text-sm">When someone sends you a connection request, it will appear here.</p>
      </div>

      <div class="space-y-3">
        <div *ngFor="let req of requests"
             class="bg-white border border-gray-200 rounded-2xl p-4 flex items-center justify-between
                    shadow-sm hover:border-indigo-200 transition-all">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-gradient-to-br from-indigo-400 to-purple-500
                        flex items-center justify-center text-white font-bold text-base select-none">
              {{ req.senderName.charAt(0) | uppercase }}
            </div>
            <div>
              <p class="font-semibold text-sm text-gray-900">{{ req.senderName }}</p>
              <p *ngIf="req.senderHeadline" class="text-xs text-gray-500 mt-0.5">{{ req.senderHeadline }}</p>
              <p class="text-xs text-gray-400 mt-0.5">{{ req.sentAt | date: 'mediumDate' }}</p>
            </div>
          </div>
          <div class="flex gap-2 shrink-0">
            <button
              (click)="accept(req)"
              [id]="'accept-' + req.id"
              [attr.aria-label]="'Accept request from ' + req.senderName"
              [disabled]="actingId === req.id"
              class="rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-medium
                     px-4 py-2 disabled:opacity-50 transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-400">
              Accept
            </button>
            <button
              (click)="reject(req)"
              [id]="'reject-' + req.id"
              [attr.aria-label]="'Reject request from ' + req.senderName"
              [disabled]="actingId === req.id"
              class="rounded-xl border border-gray-300 hover:bg-gray-100 text-gray-600 text-xs font-medium
                     px-4 py-2 disabled:opacity-50 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-300">
              Ignore
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class PendingRequestsComponent implements OnInit {
  private connectionService = inject(ConnectionService);

  requests: ConnectionRequest[] = [];
  loading = true;
  actingId: number | null = null;
  skeletons = Array(4);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.connectionService.getPendingRequests().subscribe({
      next: (res) => { this.requests = res.data ?? []; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  accept(req: ConnectionRequest): void {
    this.actingId = req.id;
    this.connectionService.acceptRequest(req.id).subscribe({
      next: () => { this.requests = this.requests.filter(r => r.id !== req.id); this.actingId = null; },
      error: () => { this.actingId = null; },
    });
  }

  reject(req: ConnectionRequest): void {
    this.actingId = req.id;
    this.connectionService.rejectRequest(req.id).subscribe({
      next: () => { this.requests = this.requests.filter(r => r.id !== req.id); this.actingId = null; },
      error: () => { this.actingId = null; },
    });
  }
}
