import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConnectionService, ConnectionRequest } from '../../../core/services/connection.service';
import { NetworkUserCardComponent } from '../network-user-card/network-user-card.component';
import { NetworkUser } from '../../../core/services/network.service';

@Component({
    selector: 'ic-sent-requests',
    standalone: true,
    imports: [CommonModule, NetworkUserCardComponent],
    template: `
    <div class="px-6 py-4">
      <h2 class="text-lg font-bold text-gray-900 mb-4">
        Sent Requests
        <span class="ml-2 text-sm font-normal text-gray-400">({{ requests.length }})</span>
      </h2>

      <div *ngIf="loading" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div *ngFor="let _ of skeletons" class="h-40 bg-gray-100 rounded-2xl animate-pulse"></div>
      </div>

      <div *ngIf="!loading && !requests.length"
           class="flex flex-col items-center justify-center py-20 text-gray-400 gap-3">
        <svg class="w-14 h-14 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
        </svg>
        <p class="font-semibold">No sent requests</p>
        <p class="text-sm">Requests you've sent will appear here until accepted or withdrawn.</p>
      </div>

      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <ic-network-user-card
          *ngFor="let req of asUsers(requests)"
          [user]="req"
          [showCancel]="true"
          (cancel)="cancel(getRequestId(req.id))"
        />
      </div>
    </div>
  `,
})
export class SentRequestsComponent implements OnInit {
    private connectionService = inject(ConnectionService);

    requests: ConnectionRequest[] = [];
    loading = true;
    skeletons = Array(6);

    ngOnInit(): void {
        this.load();
    }

    load(): void {
        this.loading = true;
        this.connectionService.getSentRequests().subscribe({
            next: (res) => { this.requests = res.data ?? []; this.loading = false; },
            error: () => { this.loading = false; },
        });
    }

    /** Map ConnectionRequest to the NetworkUser shape the card expects */
    asUsers(reqs: ConnectionRequest[]): NetworkUser[] {
        return reqs.map(r => ({
            id: r.receiverId,
            fullName: r.receiverName,
            headline: r.receiverHeadline,
            profilePicture: undefined,
            location: undefined,
            role: undefined,
            mutualConnections: 0,
            alreadyConnected: false,
            requestPending: true,
        } as NetworkUser));
    }

    getRequestId(targetUserId: number): number {
        return this.requests.find(r => r.receiverId === targetUserId)?.id ?? 0;
    }

    cancel(requestId: number): void {
        this.connectionService.cancelRequest(requestId).subscribe({
            next: () => {
                this.requests = this.requests.filter(r => r.id !== requestId);
            },
        });
    }
}
