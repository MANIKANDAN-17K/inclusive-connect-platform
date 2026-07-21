import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

export interface NetworkUser {
    id: number;
    fullName: string;
    headline?: string;
    profilePicture?: string;
    location?: string;
    role?: string;
    mutualConnections: number;
    alreadyConnected: boolean;
    requestPending: boolean;
    // Extended display fields (optional — populated when returned by backend)
    skills?: string[];
    openToWork?: boolean;
}

export interface NetworkUsersPage {
    content: NetworkUser[];
    totalElements: number;
    totalPages: number;
    number: number; // current page
    size: number;
    last: boolean;
}

@Injectable({ providedIn: 'root' })
export class NetworkService {
    private http = inject(HttpClient);
    private base = `${environment.apiUrl}/network`;

    discoverPeople(page = 0, size = 12): Observable<ApiResponse<NetworkUsersPage>> {
        return this.http.get<ApiResponse<NetworkUsersPage>>(
            `${this.base}/discover?page=${page}&size=${size}`
        );
    }

    searchUsers(keyword: string, page = 0, size = 12): Observable<ApiResponse<NetworkUsersPage>> {
        const q = encodeURIComponent(keyword.trim());
        return this.http.get<ApiResponse<NetworkUsersPage>>(
            `${this.base}/search?keyword=${q}&page=${page}&size=${size}`
        );
    }
}
