import { Component, HostListener } from '@angular/core';
import { AppRoutingModule } from "./app-routing-module";

@Component({
  selector: 'app-root',
  standalone: false,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
currentYear: number = new Date().getFullYear();
  isScrolled: boolean = false;

  ngOnInit(): void {
    console.log('Helha Job App initialized');
  }

  @HostListener('window:scroll', ['$event'])
  onScroll(event: Event): void {
    const scrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    this.isScrolled = scrollPosition > 50;
    
    const header = document.querySelector('.app-header');
    if (header) {
      if (this.isScrolled) {
        header.classList.add('scrolled');
      } else {
        header.classList.remove('scrolled');
      }
    }
  }
}
