import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccessibilityService, AccessibilityPreferences } from '../../core/services/accessibility.service';

@Component({
  selector: 'ic-accessibility-settings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './accessibility-settings.component.html',
})
export class AccessibilitySettingsComponent {
  private accessibilityService = inject(AccessibilityService);

  prefs$ = this.accessibilityService.prefs$;

  readonly fontSizes: AccessibilityPreferences['fontSize'][] = [
    'normal',
    'large',
    'extra-large'
  ];

  toggleHighContrast(current: boolean): void {
    this.accessibilityService.setHighContrast(!current);
  }

  toggleReduceMotion(current: boolean): void {
    this.accessibilityService.setReduceMotion(!current);
  }

  setFontSize(size: AccessibilityPreferences['fontSize']): void {
    this.accessibilityService.setFontSize(size);
  }

  resetAll(): void {
    this.accessibilityService.reset();
  }
} 