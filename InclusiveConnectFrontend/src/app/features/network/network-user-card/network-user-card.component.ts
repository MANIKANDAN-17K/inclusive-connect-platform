import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NetworkUser } from '../../../core/services/network.service';

@Component({
  selector: 'ic-network-user-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <article
      class="card group flex flex-col gap-0 overflow-hidden
             hover:shadow-card-hover hover:border-primary-200 hover:-translate-y-0.5
             transition-all duration-200"
      [attr.aria-label]="user.fullName + ' profile card'">

      <!-- Colored top bar -->
      <div class="h-1.5 w-full bg-primary-gradient"></div>

      <div class="p-5 flex flex-col gap-4">

        <!-- Avatar + name row -->
        <div class="flex items-start gap-3.5">
          <!-- Avatar -->
          <div class="relative shrink-0">
            <div class="avatar-initials h-14 w-14 text-xl from-primary to-secondary
                        ring-2 ring-white shadow-soft">
              {{ user.fullName.charAt(0) | uppercase }}
            </div>
            <!-- Availability dot -->
            <span
              *ngIf="user.openToWork"
              class="absolute -bottom-0.5 -right-0.5 h-4 w-4 rounded-full bg-accent
                     ring-2 ring-white flex items-center justify-center"
              title="Open to opportunities"
              aria-label="Open to work">
              <svg width="8" height="8" viewBox="0 0 8 8" fill="white" aria-hidden="true">
                <circle cx="4" cy="4" r="3"/>
              </svg>
            </span>
          </div>

          <!-- Info -->
          <div class="min-w-0 flex-1">
            <p class="font-semibold text-text-primary text-sm leading-tight truncate
                       group-hover:text-primary transition-colors">
              {{ user.fullName }}
            </p>
            <p class="text-xs text-text-secondary mt-0.5 line-clamp-2 leading-snug">
              {{ user.headline || 'Professional' }}
            </p>
            <div *ngIf="user.location"
                 class="flex items-center gap-1 mt-1.5 text-[11px] text-text-secondary">
              <svg class="w-3 h-3 shrink-0" fill="none" viewBox="0 0 24 24"
                   stroke="currentColor" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M17.657 16.657L13.414 20.9a2 2 0 01-2.828 0l-4.243-4.243A8 8 0 1118 10c0 2.8-1.3 5.3-3.343 6.657z"/>
              </svg>
              <span class="truncate">{{ user.location }}</span>
            </div>
          </div>

          <!-- Open to work badge -->
          <span *ngIf="user.openToWork"
                class="badge-accent text-[10px] shrink-0 whitespace-nowrap">
            Open to work
          </span>
        </div>

        <!-- Skills chips -->
        <div *ngIf="user.skills?.length" class="flex flex-wrap gap-1.5">
          <span *ngFor="let skill of user.skills | slice:0:4"
                class="chip text-[11px] px-2.5 py-0.5">
            {{ skill }}
          </span>
          <span *ngIf="user.skills!.length > 4"
                class="chip text-[11px] px-2.5 py-0.5 text-text-secondary/70">
            +{{ user.skills!.length - 4 }} more
          </span>
        </div>

        <!-- Mutual connections hint -->
        <p *ngIf="user.mutualConnections && user.mutualConnections > 0"
           class="text-[11px] text-text-secondary flex items-center gap-1.5">
          <svg class="w-3.5 h-3.5 text-primary shrink-0" fill="none" viewBox="0 0 24 24"
               stroke="currentColor" aria-hidden="true">
            <circle cx="9" cy="8" r="3.5"/>
            <circle cx="17" cy="9" r="2.5"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M2 20a7 7 0 0114 0M14.5 20a5 5 0 017.5-4.5"/>
          </svg>
          {{ user.mutualConnections }} mutual connection{{ user.mutualConnections === 1 ? '' : 's' }}
        </p>

        <!-- Action buttons -->
        <div class="flex gap-2 mt-auto pt-1">

          <!-- Message (connections view) -->
          <button
            *ngIf="showMessage"
            type="button"
            (click)="onMessage()"
            [id]="'message-' + user.id"
            [attr.aria-label]="'Message ' + user.fullName"
            class="btn-outline flex-1 text-xs py-1.5 px-3 rounded-lg">
            <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
            </svg>
            Message
          </button>

          <!-- Remove connection -->
          <button
            *ngIf="showRemove"
            type="button"
            (click)="onRemove()"
            [id]="'remove-' + user.id"
            [attr.aria-label]="'Remove connection with ' + user.fullName"
            class="btn-danger flex-1 text-xs py-1.5 px-3 rounded-lg">
            Remove
          </button>

          <!-- Connect / Pending -->
          <button
            *ngIf="showConnect"
            type="button"
            (click)="!user.requestPending && !user.alreadyConnected && onConnect()"
            [id]="'connect-' + user.id"
            [attr.aria-label]="connectLabel"
            [disabled]="user.requestPending || user.alreadyConnected || connecting"
            class="flex-1 inline-flex items-center justify-center gap-1.5 rounded-lg
                   text-xs font-semibold py-1.5 px-3 transition-all duration-150
                   focus-visible:outline focus-visible:outline-2"
            [ngClass]="connectClass">
            <!-- Spinner while connecting -->
            <svg *ngIf="connecting"
                 class="w-3.5 h-3.5 animate-spin" fill="none" viewBox="0 0 24 24" aria-hidden="true">
              <circle class="opacity-25" cx="12" cy="12" r="10"
                      stroke="currentColor" stroke-width="4"/>
              <path class="opacity-75" fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
            </svg>
            <!-- Plus icon for fresh connect -->
            <svg *ngIf="!connecting && !user.requestPending && !user.alreadyConnected"
                 class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24"
                 stroke="currentColor" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 5v14M5 12h14"/>
            </svg>
            {{ connectLabel }}
          </button>

          <!-- Cancel sent request -->
          <button
            *ngIf="showCancel"
            type="button"
            (click)="onCancel()"
            [id]="'cancel-' + user.id"
            [attr.aria-label]="'Cancel request to ' + user.fullName"
            class="btn-ghost flex-1 text-xs py-1.5 px-3 rounded-lg border border-border">
            Cancel Request
          </button>

        </div>
      </div>
    </article>
  `,
})
export class NetworkUserCardComponent {
  @Input({ required: true }) user!: NetworkUser;

  @Input() showConnect = false;
  @Input() showMessage = false;
  @Input() showRemove  = false;
  @Input() showCancel  = false;
  @Input() connecting  = false;

  @Output() connect = new EventEmitter<void>();
  @Output() message = new EventEmitter<void>();
  @Output() remove  = new EventEmitter<void>();
  @Output() cancel  = new EventEmitter<void>();

  get connectLabel(): string {
    if (this.user.alreadyConnected)              return 'Connected';
    if (this.user.requestPending || this.connecting) return 'Pending…';
    return 'Connect';
  }

  get connectClass(): string {
    if (this.user.alreadyConnected)
      return 'bg-accent-50 text-accent-700 border border-accent-200 cursor-default focus-visible:outline-accent';
    if (this.user.requestPending || this.connecting)
      return 'bg-amber-50 text-amber-700 border border-amber-200 cursor-default focus-visible:outline-amber-400';
    return 'bg-primary text-white shadow-btn hover:bg-primary-600 hover:shadow-btn-hover focus-visible:outline-primary';
  }

  onConnect(): void { this.connect.emit(); }
  onMessage(): void { this.message.emit(); }
  onRemove():  void { this.remove.emit(); }
  onCancel():  void { this.cancel.emit(); }
}
