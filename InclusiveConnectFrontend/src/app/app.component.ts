import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <a class="skip-link" href="#main-content">Skip to main content</a>
    <main id="main-content">
      <router-outlet></router-outlet>
    </main>
  `,
})
export class AppComponent {
  title = 'inclusive-connect-frontend';
}