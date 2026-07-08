import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface AccessibilityPreferences {
  highContrast: boolean;
  fontSize: 'normal' | 'large' | 'extra-large';
  reduceMotion: boolean;
}

const STORAGE_KEY = 'ic_accessibility_prefs';

const DEFAULT_PREFS: AccessibilityPreferences = {
  highContrast: false,
  fontSize: 'normal',
  reduceMotion: false,
};

@Injectable({ providedIn: 'root' })
export class AccessibilityService {
  private prefsSubject = new BehaviorSubject<AccessibilityPreferences>(this.loadPrefs());
  prefs$ = this.prefsSubject.asObservable();

  constructor() {
    this.applyToDocument(this.prefsSubject.value);
  }

  get current(): AccessibilityPreferences {
    return this.prefsSubject.value;
  }

  setHighContrast(enabled: boolean): void {
    this.update({ ...this.current, highContrast: enabled });
  }

  setFontSize(size: AccessibilityPreferences['fontSize']): void {
    this.update({ ...this.current, fontSize: size });
  }

  setReduceMotion(enabled: boolean): void {
    this.update({ ...this.current, reduceMotion: enabled });
  }

  reset(): void {
    this.update(DEFAULT_PREFS);
  }

  private update(prefs: AccessibilityPreferences): void {
    this.prefsSubject.next(prefs);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(prefs));
    this.applyToDocument(prefs);
  }

  private loadPrefs(): AccessibilityPreferences {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? { ...DEFAULT_PREFS, ...JSON.parse(raw) } : DEFAULT_PREFS;
    } catch {
      return DEFAULT_PREFS;
    }
  }

  private applyToDocument(prefs: AccessibilityPreferences): void {
    const body = document.body;

    body.classList.toggle('high-contrast', prefs.highContrast);
    body.classList.toggle('reduce-motion', prefs.reduceMotion);

    body.classList.remove('font-size-normal', 'font-size-large', 'font-size-extra-large');
    body.classList.add(`font-size-${prefs.fontSize}`);
  }
}