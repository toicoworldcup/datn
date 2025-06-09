import { Component, OnInit } from '@angular/core';
import { ClazzService } from '../../../services/clazz.service';
import { TeacherService } from '../../../services/teacher.service';
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Clazz } from '../../../models/clazz.model';
import { Teacher } from '../../../models/teacher.model';
import { Student } from '../../../models/student.model';
import { StudentService } from '../../../services/student.service';
import { Dkl } from '../../../models/dkl.model';
import { Semester } from '../../../models/semester.model';
import { SemesterService } from '../../../services/semester.service';

@Component({
  selector: 'app-clazz-list',
  templateUrl: './clazz-list.component.html',
  styleUrls: ['./clazz-list.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class ClazzListComponent implements OnInit {
    danhSachDangKy: Dkl[] = [];
  selectedSemester: string = '';
  errorMessage: string = '';
  hocKyOptions: Semester[] = []; // Sử dụng mảng Semester

  constructor(
    private studentService: StudentService,
    private semesterService: SemesterService // Inject SemesterService
  ) { }

  ngOnInit(): void {
    this.loadHocKyOptions();
    // Bạn có thể tải danh sách cho học kỳ mặc định nếu muốn
  }

  loadHocKyOptions(): void {
    this.semesterService.getAllSemesters().subscribe({
      next: (data: Semester[]) => {
        this.hocKyOptions = data;
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải danh sách học kỳ.';
        console.error('Lỗi tải danh sách học kỳ', error);
        this.hocKyOptions = [];
      }
    });
  }

  loadDanhSachDangKy(): void {
    if (this.selectedSemester) {
      this.studentService.getAllDangkilopByMSSV(this.selectedSemester).subscribe({
        next: (data: Dkl[]) => {
          this.danhSachDangKy = data;
          this.errorMessage = '';
        },
        error: (error) => {
          this.errorMessage = 'Không thể tải danh sách đăng ký lớp.';
          console.error('Lỗi tải danh sách đăng ký lớp', error);
          this.danhSachDangKy = [];
        }
      });
    } else {
      this.errorMessage = 'Vui lòng chọn học kỳ.';
      this.danhSachDangKy = [];
    }
  }
 
}
