import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

export interface Connection {
    userId: number;
    name: string;
    headline?: string;
    profilePicture?: string;
}

export interface ConnectionRequest {
    id: number;
    status: string;
    sentAt: string;
    senderId: number;
    senderName: string;
    senderHeadline?: string;
    receiverId: number;
    receiverName: string;
    receiverHeadline?: string;
}

@Injectable({ providedIn: 'root' })
export class ConnectionService {
    private http = inject(HttpClient);
    private baseUrl = `${environment.apiUrl}/connections`;

    getMyConnections(): Observable<ApiResponse<Connection[]>> {
        return this.http.get<ApiResponse<Connection[]>>(this.baseUrl);
    }

    getPendingRequests(): Observable<ApiResponse<ConnectionRequest[]>> {
        return this.http.get<ApiResponse<ConnectionRequest[]>>(`${this.baseUrl}/pending`);
    }

    sendRequest(userId: number): Observable<ApiResponse<ConnectionRequest>> {
        return this.http.post<ApiResponse<ConnectionRequest>>(`${this.baseUrl}/request/${userId}`, {});
    }

    acceptRequest(requestId: number): Observable<ApiResponse<ConnectionRequest>> {
        return this.http.put<ApiResponse<ConnectionRequest>>(`${this.baseUrl}/accept/${requestId}`, {});
    }

    rejectRequest(requestId: number): Observable<ApiResponse<ConnectionRequest>> {
        return this.http.put<ApiResponse<ConnectionRequest>>(`${this.baseUrl}/reject/${requestId}`, {});
    }

    getSentRequests(): Observable<ApiResponse<ConnectionRequest[]>> {
        return this.http.get<ApiResponse<ConnectionRequest[]>>(`${this.baseUrl}/sent`);
    }

    removeConnection(userId: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${userId}`);
    }

    cancelRequest(requestId: number): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/request/${requestId}`);
    }
}
