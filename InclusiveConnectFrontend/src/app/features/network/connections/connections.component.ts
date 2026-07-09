import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ConnectionService, Connection } from '../../../core/services/connection.service';
import { NetworkUser } from '../../../core/services/network.service';
import { NetworkUserCardComponent } from '../network-user-card/network-user-card.component';

@Component({
    selector: 'ic-connections',
    standalone: true,
    imports: [CommonModule, NetworkUserCardComponent],
    template: `
    <div class="px-6 py-4">
      <h2 class="text-lg font-bold text-gray-900 mb-4">
        My Connections
        <span class="ml-2 text-sm font-normal text-gray-400">({{ connections.length }})</span>
      </h2>

      <div *ngIf="loading" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div *ngFor="let _ of skeletons"
             class="h-40 bg-gray-100 rounded-2xl animate-pulse"></div>
      </div>

      <div *ngIf="!loading && !connections.length"
           class="flex flex-col items-center justify-center py-20 text-gray-400 gap-3">
        <svg class="w-14 h-14 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857
                   M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002
                   5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
        </svg>
        <p class="font-semibold">No connections yet</p>
        <p class="text-sm">Discover and connect with professionals from the Discover tab.</p>
      </div>

      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <ic-network-user-card
          *ngFor="let conn of connections"
          [user]="conn"
          [showMessage]="true"
          [showRemove]="true"
          (message)="openChat(conn)"
          (remove)="removeConnection(conn)"
        />
      </div>
    </div>
  `,
})
export class ConnectionsComponent implements OnInit {
    private connectionService = inject(ConnectionService);
    private router = inject(Router);

    connections: NetworkUser[] = [];
    loading = true;
    skeletons = Array(6);

    ngOnInit(): void {
        this.loadConnections();
    }

    loadConnections(): void {
        this.loading = true;
        this.connectionService.getMyConnections().subscribe({
            next: (res) => {
                this.connections = (res.data ?? []).map(c => ({
                    id: c.userId,
                    fullName: c.name,
                    headline: c.headline,
                    profilePicture: c.profilePicture,
                    location: undefined,
                    role: undefined,
                    mutualConnections: 0,
                    alreadyConnected: true,
                    requestPending: false,
                } as NetworkUser));
                this.loading = false;
            },
            error: () => { this.loading = false; },
        });
    }

    openChat(conn: NetworkUser): void {
        this.router.navigate(['/chat'], { queryParams: { userId: conn.id, name: conn.fullName } });
    }

    removeConnection(conn: NetworkUser): void {
        this.connectionService.removeConnection(conn.id).subscribe({
            next: () => {
                this.connections = this.connections.filter(c => c.id !== conn.id);
            },
        });
    }
}
