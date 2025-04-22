import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule } from '@angular/forms';
import { TeacherService } from '../../services/teacher.service';
import { ClazzService } from '../../services/clazz.service';
import { SemesterService } from '../../services/semester.service';
import { AssignmentService } from '../../services/assignment.service';
@Component({
  selector: 'app-assign-teacher',
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
  templateUrl: './assign-teacher.component.html',
  styleUrls: ['./assign-teacher.component.css'] // ✅ Phải là mảng
})
export class AssignTeacherComponent implements OnInit   {
  teacherList: string[] = [];
  classList: string[] = [];
  semesterList: string[] = [];

  assignMaGv: string = '';
  assignMaLop: string = '';
  assignHocKi: string = '';
  constructor(
    private teacherService: TeacherService,
    private classService: ClazzService,
    private semesterService: SemesterService,
    private assignmentService: AssignmentService // nếu bạn có service riêng cho gán
  ) {}
  
  ngOnInit() {
    // Load danh sách để hiển thị trong datalist
    this.teacherService.getAllTeachers().subscribe(data => this.teacherList = data.map(t => t.maGv));
    this.classService.getAllClazzes().subscribe(data => this.classList = data.map(c => c.maLop));
    this.semesterService.getAllSemesters().subscribe(data => this.semesterList = data.map(s => s.name));
  }
  assignTeacher(): void {
    const assignment = {
      
      maLop: this.assignMaLop,
      hocKi: this.assignHocKi,
      maGv: this.assignMaGv,
    };

    this.assignmentService.assignTeacherClazz(
      this.assignMaLop,
      this.assignHocKi,
      this.assignMaGv
    ).subscribe({
      next: () => {
        alert('Gán giáo viên thành công!');
        this.assignMaGv = '';
        this.assignMaLop = '';
        this.assignHocKi = '';
      },
      error: err => {
        console.error('Lỗi khi gán giáo viên:', err);
        alert('Có lỗi xảy ra khi gán giáo viên.');
      }
    });
    
  }
  

}
