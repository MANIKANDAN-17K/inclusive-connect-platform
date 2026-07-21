import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './layouts/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent],
  template: `
    <a class="skip-link" href="#main-content">Skip to main content</a>
    <ic-navbar></ic-navbar>
    <main id="main-content" class="min-h-[calc(100vh-4rem)]">
      <router-outlet></router-outlet>
    </main>
  `,
})
export class AppComponent {
  title = 'InclusiveConnect';
}
