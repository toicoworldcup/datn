import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-qldt',
  standalone: true, // Ensure this is true
  imports: [
   RouterModule  // ... other standalone components or modules AdminComponent uses
  ],
  templateUrl: './qldt.component.html',
  styleUrls: ['./qldt.component.css']
})
export class QldtComponent {
  constructor() { }
}
