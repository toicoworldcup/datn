import { Component, OnInit } from '@angular/core';
import { Clazz } from '../../models/clazz.model';
import { Teacher } from '../../models/teacher.model';
import { ClazzService } from '../../../services/clazz.service';
import { TeacherService } from '../../../services/teacher.service';
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";


@Component({
  selector: 'app-clazz-list',
  templateUrl: './clazz-list.component.html',
  styleUrls: ['./clazz-list.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class ClazzListComponent implements OnInit {
  clazzes: Clazz[] = [];
  teachers: Teacher[] = [];
  p: number = 1;
  isAddClazzModalVisible: boolean = false;
  newClazz: Clazz = { maLop: '', maHocPhan: '', hocki: '' };

  constructor(
    private clazzService: ClazzService,
    private teacherService: TeacherService
  ) { }

  ngOnInit(): void {
    this.loadClazzes();
    this.loadTeachers(); // Tải danh sách giáo viên để có thể hiển thị tên
  }

  loadClazzes(): void {
    this.clazzService.getAllClazzes().subscribe(
      (data: Clazz[]) => {
        this.clazzes = data;
      },
      (error) => {
        console.error('Lỗi khi tải danh sách lớp học:', error);
      }
    );
  }

  loadTeachers(): void {
    this.teacherService.getAllTeachers().subscribe(
      (data: Teacher[]) => {
        this.teachers = data;
      },
      (error) => {
        console.error('Lỗi khi tải danh sách giáo viên:', error);
      }
    );
  }

  toggleAddClazzModal(): void {
    this.isAddClazzModalVisible = !this.isAddClazzModalVisible;
    this.newClazz = { maLop: '', maHocPhan: '', hocki: '' }; // Reset form khi mở
  }

  addClazz(): void {
    this.clazzService.addClazz(this.newClazz).subscribe(
      (response) => {
        console.log('Thêm lớp thành công:', response);
        this.loadClazzes(); // Tải lại danh sách lớp sau khi thêm
        this.toggleAddClazzModal(); // Đóng modal
      },
      (error) => {
        console.error('Lỗi khi thêm lớp:', error);
      }
    );
  }

  getTeacherName(teachers: any[] | undefined): string {
    if (!teachers || teachers.length === 0) {
      return 'Chưa có';
    }
    const teacherNames = teachers.map(teacher => teacher.name);
    return teacherNames.join(', '); // Nối tên các giáo viên bằng dấu phẩy và khoảng trắng
  }
}