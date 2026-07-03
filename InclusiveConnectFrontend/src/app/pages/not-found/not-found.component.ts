import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="min-h-screen flex flex-col items-center justify-center text-center px-4">
      <h1 class="text-3xl font-semibold">Page not found</h1>
      <a routerLink="/" class="mt-4 text-primary underline">Go back home</a>
    </section>
  `,
})
export class NotFoundComponent {}