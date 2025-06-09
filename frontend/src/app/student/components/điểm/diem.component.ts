import { Component, OnInit } from "@angular/core";
import { ClazzService } from "../../../services/clazz.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { SemesterService } from "../../../services/semester.service";
import { CourseService } from "../../../services/course.service";
import { Ctdt } from "../../../models/ctdt.model";
import { CTDTService } from "../../../services/ctdt.service";
import { Schedule } from "../../../models/schedule.model";
import { TeacherService } from "../../../services/teacher.service";
import { NgxPaginationModule } from "ngx-pagination";
import { StudentService } from "../../../services/student.service";
import { Semester } from "../../../models/semester.model";

@Component({
  selector: "app-diem",
  templateUrl: "./diem.component.html",
  styleUrls: ["./diem.component.css"],
  standalone: true,
  imports: [CommonModule, FormsModule, NgxPaginationModule],
})
export class DiemComponent implements OnInit {
  selectedSemester: string = '';
  grades: any[] = [];
  errorMessage: string = '';
    isDataLoaded: boolean = false; // Thêm biến này

    hocKyOptions: Semester[] = []; // Sử dụng mảng Semester


constructor(
    private studentService: StudentService,
    private semesterService: SemesterService // Inject SemesterService
  ) { }
  ngOnInit(): void {
        this.loadHocKyOptions();

    // Bạn có thể load danh sách học kỳ ở đây nếu cần
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

  loadGrades(): void {
  this.isDataLoaded = true;

  if (this.selectedSemester) {
    this.studentService.getMyGradesBySemester(this.selectedSemester).subscribe({
      next: (response) => {
        if (response === 'Chưa có điểm cho kỳ này.') {
          this.grades = [];
          this.errorMessage = 'Không có điểm nào trong học kỳ này.';
        } else {
          try {
            this.grades = JSON.parse(response);
            this.errorMessage = '';
          } catch (error) {
            console.error('Lỗi parse JSON:', error);
            this.errorMessage = 'Lỗi khi xử lý dữ liệu điểm.';
            this.grades = [];
          }
        }
      },
      error: (error) => {
        this.errorMessage = 'Không thể tải điểm.';
        this.grades = [];
        console.error('Lỗi tải điểm:', error);
      }
    });
  } else {
    this.grades = [];
    this.errorMessage = 'Vui lòng chọn học kỳ.';
  }
}
}