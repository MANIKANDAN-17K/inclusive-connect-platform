import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ConnectionService, Connection, ConnectionRequest } from '../../core/services/connection.service';

@Component({
    selector: 'ic-network',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './network.component.html',
})
export class NetworkComponent implements OnInit {
    private connectionService = inject(ConnectionService);
    private router = inject(Router);

    connections: Connection[] = [];
    pendingRequests: ConnectionRequest[] = [];
    loadingConnections = true;
    loadingPending = true;
    acceptingId: number | null = null;

    ngOnInit(): void {
        this.loadConnections();
        this.loadPendingRequests();
    }

    loadConnections(): void {
        this.loadingConnections = true;
        this.connectionService.getMyConnections().subscribe({
            next: (res) => {
                this.connections = res.data ?? [];
                this.loadingConnections = false;
            },
            error: () => {
                this.loadingConnections = false;
            },
        });
    }

    loadPendingRequests(): void {
        this.loadingPending = true;
        this.connectionService.getPendingRequests().subscribe({
            next: (res) => {
                this.pendingRequests = res.data ?? [];
                this.loadingPending = false;
            },
            error: () => {
                this.loadingPending = false;
            },
        });
    }

    acceptRequest(request: ConnectionRequest): void {
        this.acceptingId = request.id;
        this.connectionService.acceptRequest(request.id).subscribe({
            next: () => {
                this.pendingRequests = this.pendingRequests.filter((r) => r.id !== request.id);
                this.acceptingId = null;
                this.loadConnections(); // refresh connections list
            },
            error: () => {
                this.acceptingId = null;
            },
        });
    }

    rejectRequest(request: ConnectionRequest): void {
        this.connectionService.rejectRequest(request.id).subscribe({
            next: () => {
                this.pendingRequests = this.pendingRequests.filter((r) => r.id !== request.id);
            },
        });
    }

    messageConnection(connection: Connection): void {
        this.router.navigate(['/chat'], {
            queryParams: { userId: connection.userId, name: connection.name },
        });
    }
}
