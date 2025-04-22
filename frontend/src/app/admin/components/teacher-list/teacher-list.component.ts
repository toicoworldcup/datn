import { Component, OnInit } from '@angular/core';
import { TeacherService } from '../../services/teacher.service';
import { CommonModule } from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule } from '@angular/forms';
import { Teacher } from '../../models/teacher.model';

@Component({
  selector: 'app-teacher-list',
  templateUrl: './teacher-list.component.html',
  styleUrls: ['./teacher-list.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class TeacherListComponent implements OnInit {
  teachers: Teacher[] = [];
  p: number = 1;
  isAddTeacherModalVisible: boolean = false;

  newTeacher: any = {
    // 👈 Thêm dòng này
    maGv: '',
    name: '',
    email: '',
    phoneNumber: '',
    dateOfBirth: '',
    address: '',
    departmentName: '',
    gender: '',
    cccd: '',
  };
  resetNewTeacher(): void {
    this.newTeacher = {
      maGv: '',
      name: '',
      email: '',
      phoneNumber: '',
      dateOfBirth: '',
      address: '',
      departmentName: '',
      gender: 'Nam',
      cccd: '',
    };
  }

  constructor(private teacherService: TeacherService) {}

  ngOnInit(): void {
    this.loadTeachers();
  }
  toggleAddTeacherModal(): void {
    this.isAddTeacherModalVisible = !this.isAddTeacherModalVisible;
    console.log('Modal visibility:', this.isAddTeacherModalVisible);

    // Nếu đóng modal, reset lại biểu mẫu
    if (!this.isAddTeacherModalVisible) {
      this.resetNewTeacher();
    }
  }

  loadTeachers(): void {
    this.teacherService.getAllTeachers().subscribe((data) => {
      this.teachers = data;
    });
  }
  pageChanged(page: number): void {
    this.p = page;
    this.loadTeachers(); // Tải lại danh sách giáo viên sau khi thay đổi trang
  }

  deleteTeacher(maGv: string): void {
    if (confirm('Bạn có chắc chắn muốn xóa giáo viên này không?')) {
      this.teacherService.deleteTeacher(maGv).subscribe(() => {
        this.loadTeachers();
      });
    }
  }
  addTeacher(): void {
    this.teacherService.addTeacher(this.newTeacher).subscribe({
      next: (data) => {
        this.teachers.push(data); // Thêm giáo viên mới vào danh sách
        this.toggleAddTeacherModal(); // Đóng modal
        this.newTeacher = {}; // Làm sạch form
      },
      error: (error) => {
        console.error('Có lỗi khi thêm giáo viên', error);
      },
    });
  }
}
