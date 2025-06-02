import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-admin',
  standalone: true, // Ensure this is true
  imports: [
   RouterModule,HeaderComponent    // ... other standalone components or modules AdminComponent uses
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent {
  constructor() { }
}
