import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-student',
  standalone: true,
  templateUrl: './student.component.html',
  imports: [
    RouterModule  // ... other standalone components or modules AdminComponent uses
   ],
  styleUrl: './student.component.css'
})
export class StudentComponent {
  constructor() { }

}
