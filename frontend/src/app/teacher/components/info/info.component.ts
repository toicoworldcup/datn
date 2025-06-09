import { Component, OnInit } from '@angular/core';
import { Teacher } from '../../../models/teacher.model';
import { TeacherService } from '../../../services/teacher.service';
import { NgIf } from '@angular/common'; // Import NgIf

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css'],
    imports: [NgIf] // Thêm NgIf vào imports array

})
export class InfoComponent implements OnInit {
  teacherInfo: Teacher | null = null;
  errorMessage: string = '';

  constructor(private teacherService: TeacherService) { }

  ngOnInit(): void {
    this.loadTeacherInfo();
  }

  loadTeacherInfo(): void {
    this.teacherService.getLoggedInTeacherInfo().subscribe({
      next: (data: Teacher) => {
        this.teacherInfo = data;
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải thông tin sinh viên.';
        console.error('Lỗi tải thông tin sinh viên', error);
      }
    });
  }
}