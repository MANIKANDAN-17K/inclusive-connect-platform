import { Component, Input } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { IC_ICONS, IcIconName } from '../../icons/icons';

@Component({
  selector: 'ic-icon',
  standalone: true,
  template: `
    <svg
      [attr.width]="size"
      [attr.height]="size"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="1.75"
      stroke-linecap="round"
      stroke-linejoin="round"
      [attr.aria-hidden]="ariaLabel ? null : true"
      [attr.aria-label]="ariaLabel || null"
      [attr.role]="ariaLabel ? 'img' : null"
      class="shrink-0"
      [innerHTML]="svgContent"
    ></svg>
  `,
})
export class IconComponent {
  @Input({ required: true }) name!: IcIconName;
  @Input() size: number | string = 24;
  @Input() ariaLabel = '';

  constructor(private sanitizer: DomSanitizer) {}

  get svgContent(): SafeHtml {
    const paths = IC_ICONS[this.name] ?? '';
    return this.sanitizer.bypassSecurityTrustHtml(paths);
  }
}
