import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-teacher',
  standalone: true, // Ensure this is true
  imports: [
    RouterModule  // ... other standalone components or modules AdminComponent uses
   ],
  templateUrl: './teacher.component.html',
  styleUrl: './teacher.component.css'
})
export class TeacherComponent {
  constructor() { }


}
