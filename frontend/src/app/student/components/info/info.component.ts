import { Component, OnInit } from '@angular/core';
import { Student } from '../../../models/student.model';
import { StudentService } from '../../../services/student.service';
import { NgIf } from '@angular/common'; // Import NgIf

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css'],
    imports: [NgIf] // Thêm NgIf vào imports array

})
export class InfoComponent implements OnInit {
  studentInfo: Student | null = null;
  errorMessage: string = '';

  constructor(private studentService: StudentService) { }

  ngOnInit(): void {
    this.loadStudentInfo();
  }

  loadStudentInfo(): void {
    this.studentService.getCurrentStudentInfo().subscribe({
      next: (data: Student) => {
        this.studentInfo = data;
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải thông tin sinh viên.';
        console.error('Lỗi tải thông tin sinh viên', error);
      }
    });
  }
}