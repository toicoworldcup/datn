import { Component, OnInit } from '@angular/core';
import { ClazzService } from '../../../services/clazz.service';
import { TeacherService } from '../../../services/teacher.service';
import { CommonModule } from "@angular/common";
import { NgxPaginationModule } from "ngx-pagination";
import { FormsModule } from "@angular/forms";
import { Clazz } from '../../../models/clazz.model';
import { Teacher } from '../../../models/teacher.model';
import { SemesterService } from '../../../services/semester.service'; // Import SemesterService
import { Semester } from '../../../models/semester.model'; // Import Semester model

@Component({
  selector: 'app-clazz-list',
  templateUrl: './clazz-list.component.html',
  styleUrls: ['./clazz-list.component.css'],
  standalone: true,
  imports: [CommonModule, NgxPaginationModule, FormsModule],
})
export class ClazzListComponent implements OnInit {
  classes: Clazz[] = [];
  selectedSemester: string = '';
  semesters: Semester[] = []; // Thay đổi kiểu dữ liệu của semesters
  loading: boolean = false;
  error: string = '';
  p: number = 1; // Trang hiện tại cho phân trang

  constructor(
    private teacherService: TeacherService,
    private semesterService: SemesterService // Inject SemesterService
  ) {}

  ngOnInit(): void {
    this.loadSemesters(); // Tải danh sách học kỳ khi component khởi tạo
  }

  loadSemesters(): void {
    this.loading = true;
    this.semesterService.getAllSemesters().subscribe({
      next: (data) => {
        this.semesters = data; // Lưu trữ dữ liệu học kỳ lấy được
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Lỗi khi tải danh sách học kỳ: ' + err.message;
        this.loading = false;
      },
    });
  }

  loadClasses(): void {
    if (!this.selectedSemester) {
      this.error = 'Vui lòng chọn một học kỳ.';
      this.classes = [];
      return;
    }

    this.loading = true;
    this.error = '';
    this.teacherService.getTeacherClassesBySemester(this.selectedSemester).subscribe({
      next: (data: Clazz[]) => {
        this.classes = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Có lỗi xảy ra khi tải danh sách lớp học: ' + err.message;
        this.loading = false;
        this.classes = [];
      },
    });
  }
}
