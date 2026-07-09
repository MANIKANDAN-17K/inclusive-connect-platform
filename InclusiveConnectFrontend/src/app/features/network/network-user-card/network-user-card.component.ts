import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NetworkUser } from '../../../core/services/network.service';

@Component({
  selector: 'ic-network-user-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white border border-gray-200 rounded-2xl p-5 flex flex-col gap-4
                shadow-sm hover:shadow-md hover:border-indigo-200 transition-all duration-200">

      <!-- Avatar + info -->
      <div class="flex items-start gap-3">
        <div class="w-14 h-14 rounded-full shrink-0 flex items-center justify-center text-xl font-bold text-white
                    bg-gradient-to-br from-indigo-500 to-purple-600 select-none">
          {{ user.fullName.charAt(0) | uppercase }}
        </div>
        <div class="min-w-0 flex-1">
          <p class="font-semibold text-gray-900 text-sm leading-tight truncate">{{ user.fullName }}</p>
          <p class="text-xs text-gray-500 mt-0.5 line-clamp-2">{{ user.headline || 'No headline' }}</p>
          <p *ngIf="user.location" class="text-xs text-gray-400 mt-1 flex items-center gap-1">
            <svg class="w-3 h-3 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
            {{ user.location }}
          </p>
        </div>
      </div>

      <!-- Action buttons -->
      <div class="flex gap-2">

        <!-- Message button (for connections) -->
        <button
          *ngIf="showMessage"
          (click)="onMessage()"
          [id]="'message-' + user.id"
          [attr.aria-label]="'Message ' + user.fullName"
          class="flex-1 rounded-xl bg-indigo-50 hover:bg-indigo-100 text-indigo-700 text-xs font-medium
                 py-2 transition-colors flex items-center justify-center gap-1.5 focus:outline-none focus:ring-2 focus:ring-indigo-400">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
          </svg>
          Message
        </button>

        <!-- Remove connection button -->
        <button
          *ngIf="showRemove"
          (click)="onRemove()"
          [id]="'remove-' + user.id"
          [attr.aria-label]="'Remove connection with ' + user.fullName"
          class="flex-1 rounded-xl border border-red-200 hover:bg-red-50 text-red-500 text-xs font-medium
                 py-2 transition-colors flex items-center justify-center gap-1.5 focus:outline-none focus:ring-2 focus:ring-red-300">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M13 7a4 4 0 11-8 0 4 4 0 018 0zM9 14a6 6 0 00-6 6v1h12v-1a6 6 0 00-6-6zM21 12h-6"/>
          </svg>
          Remove
        </button>

        <!-- Connect / Pending button for discover/search -->
        <button
          *ngIf="showConnect"
          (click)="!user.requestPending && !user.alreadyConnected && onConnect()"
          [id]="'connect-' + user.id"
          [attr.aria-label]="connectLabel"
          [disabled]="user.requestPending || user.alreadyConnected || connecting"
          class="flex-1 rounded-xl text-xs font-medium py-2 transition-colors flex items-center
                 justify-center gap-1.5 focus:outline-none focus:ring-2"
          [class]="connectClass">
          <svg *ngIf="!user.requestPending && !user.alreadyConnected && !connecting"
               class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
          </svg>
          <svg *ngIf="user.requestPending || connecting"
               class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-75" fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
          </svg>
          {{ connectLabel }}
        </button>

        <!-- Cancel request button (sent requests) -->
        <button
          *ngIf="showCancel"
          (click)="onCancel()"
          [id]="'cancel-' + user.id"
          [attr.aria-label]="'Cancel request to ' + user.fullName"
          class="flex-1 rounded-xl border border-gray-300 hover:bg-gray-100 text-gray-600 text-xs font-medium
                 py-2 transition-colors flex items-center justify-center gap-1.5 focus:outline-none focus:ring-2 focus:ring-gray-300">
          Cancel Request
        </button>

      </div>
    </div>
  `,
})
export class NetworkUserCardComponent {
  @Input({ required: true }) user!: NetworkUser;

  /** Show different button combinations depending on context */
  @Input() showConnect = false;
  @Input() showMessage = false;
  @Input() showRemove = false;
  @Input() showCancel = false;
  @Input() connecting = false;

  @Output() connect = new EventEmitter<void>();
  @Output() message = new EventEmitter<void>();
  @Output() remove = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  get connectLabel(): string {
    if (this.user.alreadyConnected) return 'Connected';
    if (this.user.requestPending || this.connecting) return 'Pending…';
    return 'Connect';
  }

  get connectClass(): string {
    if (this.user.alreadyConnected)
      return 'bg-green-50 text-green-600 border border-green-200 cursor-default focus:ring-green-300';
    if (this.user.requestPending || this.connecting)
      return 'bg-amber-50 text-amber-600 border border-amber-200 cursor-default focus:ring-amber-300';
    return 'bg-indigo-600 hover:bg-indigo-700 text-white focus:ring-indigo-400';
  }

  onConnect(): void { this.connect.emit(); }
  onMessage(): void { this.message.emit(); }
  onRemove(): void { this.remove.emit(); }
  onCancel(): void { this.cancel.emit(); }
}
